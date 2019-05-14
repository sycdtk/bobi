//copy from net/http/server.go
//golang version : 1.12
package mux

import (
	"net"
	"net/http"
	"net/url"
	"path"
	"regexp"
	"sort"
	"strings"
	"sync"
)

// ServeMux is an HTTP request multiplexer.
// It matches the URL of each incoming request against a list of registered
// patterns and calls the handler for the pattern that
// most closely matches the URL.
//
// Patterns name fixed, rooted paths, like "/favicon.ico",
// or rooted subtrees, like "/images/" (note the trailing slash).
// Longer patterns take precedence over shorter ones, so that
// if there are handlers registered for both "/images/"
// and "/images/thumbnails/", the latter handler will be
// called for paths beginning "/images/thumbnails/" and the
// former will receive requests for any other paths in the
// "/images/" subtree.
//
// Note that since a pattern ending in a slash names a rooted subtree,
// the pattern "/" matches all paths not matched by other registered
// patterns, not just the URL with Path == "/".
//
// If a subtree has been registered and a request is received naming the
// subtree root without its trailing slash, ServeMux redirects that
// request to the subtree root (adding the trailing slash). This behavior can
// be overridden with a separate registration for the path without
// the trailing slash. For example, registering "/images/" causes ServeMux
// to redirect a request for "/images" to "/images/", unless "/images" has
// been registered separately.
//
// Patterns may optionally begin with a host name, restricting matches to
// URLs on that host only. Host-specific patterns take precedence over
// general patterns, so that a handler might register for the two patterns
// "/codesearch" and "codesearch.google.com/" without also taking over
// requests for "http://www.google.com/".
//
// ServeMux also takes care of sanitizing the URL request path and the Host
// header, stripping the port number and redirecting any request containing . or
// .. elements or repeated slashes to an equivalent, cleaner URL.
type ServeMux struct {
	mu    sync.RWMutex
	m     map[string]muxEntry
	rm    map[string]muxEntry //正则路由
	es    []muxEntry          // slice of entries sorted from longest to shortest.
	hosts bool                // whether any patterns contain hostnames
}

type muxEntry struct {
	h          Handler
	pattern    string
	method     string         //请求类型
	pathParams map[int]string //路由参数
	auth       bool           //认证信息
}

// NewServeMux allocates and returns a new ServeMux.
func NewServeMux() *ServeMux { return new(ServeMux) }

// cleanPath returns the canonical path for p, eliminating . and .. elements.
func cleanPath(p string) string {
	if p == "" {
		return "/"
	}
	if p[0] != '/' {
		p = "/" + p
	}
	np := path.Clean(p)
	// path.Clean removes trailing slash except for root;
	// put the trailing slash back if necessary.
	if p[len(p)-1] == '/' && np != "/" {
		// Fast path for common case of p being the string we want:
		if len(p) == len(np)+1 && strings.HasPrefix(p, np) {
			np = p
		} else {
			np += "/"
		}
	}
	return np
}

// stripHostPort returns h without any trailing ":<port>".
func stripHostPort(h string) string {
	// If no port on host, return unchanged
	if strings.IndexByte(h, ':') == -1 {
		return h
	}
	host, _, err := net.SplitHostPort(h)
	if err != nil {
		return h // on error, return unchanged
	}
	return host
}

// Find a handler on a handler map given a path string.
// Most-specific (longest) pattern wins.
func (mux *ServeMux) match(path string, r *http.Request) (Handler, string, map[string]string) {

	// Check for exact match first.
	v, ok := mux.m[path]
	if ok {
		if r.Method == v.method {
			return v.h, v.pattern, nil
		}
	}

	//增加路径参数匹配
	for regexpPattern, me := range mux.rm {
		patternData := strings.Split(regexpPattern, "/")
		pathData := strings.Split(path, "/")
		if len(patternData) == len(pathData) {

			if params := regexp.MustCompile(regexpPattern).FindStringSubmatch(path); len(params) > 0 {
				if r.Method == me.method {

					paramsMap := map[string]string{}

					for i, p := range params {
						if i == 0 {
							continue
						}
						if v, ok := me.pathParams[i]; ok {
							paramsMap[v] = p
						}
					}

					return me.h, me.pattern, paramsMap
				}
			}
		}
	}

	// Check for longest valid match.  mux.es contains all patterns
	// that end in / sorted from longest to shortest.
	for _, e := range mux.es {
		if strings.HasPrefix(path, e.pattern) {
			if r.Method == e.method {
				return e.h, e.pattern, nil
			}
		}
	}
	return nil, "", nil
}

// redirectToPathSlash determines if the given path needs appending "/" to it.
// This occurs when a handler for path + "/" was already registered, but
// not for path itself. If the path needs appending to, it creates a new
// URL, setting the path to u.Path + "/" and returning true to indicate so.
func (mux *ServeMux) redirectToPathSlash(host, path string, u *url.URL) (*url.URL, bool) {
	mux.mu.RLock()
	shouldRedirect := mux.shouldRedirectRLocked(host, path)
	mux.mu.RUnlock()
	if !shouldRedirect {
		return u, false
	}
	path = path + "/"
	u = &url.URL{Path: path, RawQuery: u.RawQuery}
	return u, true
}

// shouldRedirectRLocked reports whether the given path and host should be redirected to
// path+"/". This should happen if a handler is registered for path+"/" but
// not path -- see comments at ServeMux.
func (mux *ServeMux) shouldRedirectRLocked(host, path string) bool {
	p := []string{path, host + path}

	for _, c := range p {
		if _, exist := mux.m[c]; exist {
			return false
		}
	}

	n := len(path)
	if n == 0 {
		return false
	}
	for _, c := range p {
		if _, exist := mux.m[c+"/"]; exist {
			return path[n-1] != '/'
		}
	}

	return false
}

// Handler returns the handler to use for the given request,
// consulting r.Method, r.Host, and r.URL.Path. It always returns
// a non-nil handler. If the path is not in its canonical form, the
// handler will be an internally-generated handler that redirects
// to the canonical path. If the host contains a port, it is ignored
// when matching handlers.
//
// The path and host are used unchanged for CONNECT requests.
//
// Handler also returns the registered pattern that matches the
// request or, in the case of internally-generated redirects,
// the pattern that will match after following the redirect.
//
// If there is no registered handler that applies to the request,
// Handler returns a ``page not found'' handler and an empty pattern.
func (mux *ServeMux) Handler(r *http.Request) (h Handler, pattern string, paramsMap map[string]string) {

	// CONNECT requests are not canonicalized.
	if r.Method == "CONNECT" {
		// If r.URL.Path is /tree and its handler is not registered,
		// the /tree -> /tree/ redirect applies to CONNECT requests
		// but the path canonicalization does not.
		if u, ok := mux.redirectToPathSlash(r.URL.Host, r.URL.Path, r.URL); ok {
			return RedirectHandler(u.String(), http.StatusMovedPermanently), u.Path, nil
		}

		return mux.handler(r.Host, r.URL.Path, r)
	}

	// All other requests have any port stripped and path cleaned
	// before passing to mux.handler.
	host := stripHostPort(r.Host)
	path := cleanPath(r.URL.Path)

	// If the given path is /tree and its handler is not registered,
	// redirect for /tree/.
	if u, ok := mux.redirectToPathSlash(host, path, r.URL); ok {
		return RedirectHandler(u.String(), http.StatusMovedPermanently), u.Path, nil
	}

	if path != r.URL.Path {
		_, pattern, _ = mux.handler(host, path, r)
		url := *r.URL
		url.Path = path
		return RedirectHandler(url.String(), http.StatusMovedPermanently), pattern, nil
	}

	return mux.handler(host, r.URL.Path, r)
}

// handler is the main implementation of Handler.
// The path is known to be in canonical form, except for CONNECT methods.
func (mux *ServeMux) handler(host, path string, r *http.Request) (h Handler, pattern string, paramsMap map[string]string) {
	mux.mu.RLock()
	defer mux.mu.RUnlock()

	// Host-specific pattern takes precedence over generic ones
	if mux.hosts {
		h, pattern, paramsMap = mux.match(host+path, r)
	}
	if h == nil {
		h, pattern, paramsMap = mux.match(path, r)
	}
	if h == nil {
		h, pattern, paramsMap = NotFoundHandler(), "", nil
	}
	return
}

// ServeHTTP dispatches the request to the handler whose
// pattern most closely matches the request URL.
func (mux *ServeMux) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	if r.RequestURI == "*" {
		if r.ProtoAtLeast(1, 1) {
			w.Header().Set("Connection", "close")
		}
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	h, _, paramsMap := mux.Handler(r)

	h.ServeHTTP(w, r, paramsMap)
}

// Handle registers the handler for the given pattern.
// If a handler already exists for pattern, Handle panics.
func (mux *ServeMux) Handle(pattern string, handler Handler, method string, auth bool) {
	mux.mu.Lock()
	defer mux.mu.Unlock()

	if pattern == "" {
		panic("http: invalid pattern")
	}
	if handler == nil {
		panic("http: nil handler")
	}

	paramsMap := urlParse(pattern)

	if len(paramsMap) > 0 {

		regexpPattern := regexp.MustCompile("{(.+?)}").ReplaceAllString(pattern, "(.+)")

		//路由参数路径
		if _, exist := mux.rm[regexpPattern]; exist {
			panic("http: multiple registrations for " + pattern)
		}

		if mux.rm == nil {
			mux.rm = make(map[string]muxEntry)
		}

		e := muxEntry{h: handler, pattern: pattern, method: method, auth: auth, pathParams: paramsMap}
		mux.rm[regexpPattern] = e

		if pattern[0] != '/' {
			mux.hosts = true
		}

	} else {
		//固定路径
		if _, exist := mux.m[pattern]; exist {
			panic("http: multiple registrations for " + pattern)
		}

		if mux.m == nil {
			mux.m = make(map[string]muxEntry)
		}

		e := muxEntry{h: handler, pattern: pattern, method: method, auth: auth}
		mux.m[pattern] = e
		if len(pattern) != 1 && pattern[len(pattern)-1] == '/' {
			mux.es = appendSorted(mux.es, e)
		}

		if pattern[0] != '/' {
			mux.hosts = true
		}
	}
}

//路由检查判断
func urlParse(pattern string) map[int]string {
	params := regexp.MustCompile("{(.+?)}").FindStringSubmatch(pattern)
	paramsMap := map[int]string{}

	for i, param := range params {
		if i == 0 {
			continue
		}
		paramsMap[i] = param
	}

	return paramsMap
}

func appendSorted(es []muxEntry, e muxEntry) []muxEntry {
	n := len(es)
	i := sort.Search(n, func(i int) bool {
		return len(es[i].pattern) < len(e.pattern)
	})
	if i == n {
		return append(es, e)
	}
	// we now know that i points at where we want to insert
	es = append(es, muxEntry{}) // try to grow the slice in place, any entry works.
	copy(es[i+1:], es[i:])      // Move shorter entries down
	es[i] = e
	return es
}

// HandleFunc registers the handler function for the given pattern.
func (mux *ServeMux) HandleFunc(pattern string, handler func(http.ResponseWriter, *http.Request, map[string]string), method string, auth bool) {
	if handler == nil {
		panic("http: nil handler")
	}
	mux.Handle(pattern, HandlerFunc(handler), method, auth)
}

// The HandlerFunc type is an adapter to allow the use of
// ordinary functions as HTTP handlers. If f is a function
// with the appropriate signature, HandlerFunc(f) is a
// Handler that calls f.
type HandlerFunc func(http.ResponseWriter, *http.Request, map[string]string)

// ServeHTTP calls f(w, r).
func (f HandlerFunc) ServeHTTP(w http.ResponseWriter, r *http.Request, paramsMap map[string]string) {
	f(w, r, paramsMap)
}

type Handler interface {
	ServeHTTP(http.ResponseWriter, *http.Request, map[string]string)
}

func RedirectHandler(url string, code int) Handler {
	return &redirectHandler{url, code}
}

type redirectHandler struct {
	url  string
	code int
}

func (rh *redirectHandler) ServeHTTP(w http.ResponseWriter, r *http.Request, paramsMap map[string]string) {
	http.Redirect(w, r, rh.url, rh.code)
}

// NotFound replies to the request with an HTTP 404 not found error.
func NotFound(w http.ResponseWriter, r *http.Request, paramsMap map[string]string) {
	http.Error(w, "404 page not found", http.StatusNotFound)
}

// NotFoundHandler returns a simple request handler
// that replies to each request with a ``404 page not found'' reply.
func NotFoundHandler() Handler { return HandlerFunc(NotFound) }
