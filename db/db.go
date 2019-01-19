package db

import (
	"database/sql"
	"fmt"
	"log"
	"strconv"

	"github.com/sycdtk/bobi/config"
	"github.com/sycdtk/bobi/errtools"

	_ "github.com/lib/pq"
)

var dbConn *sql.DB

func init() {
	connStr := config.Read("postgres", "conn")

	maxOpenConns, err := strconv.Atoi(config.Read("postgres", "maxOpenConns"))
	errtools.CheckErr(err, "read maxOpenConns error")

	maxIdleConns, err := strconv.Atoi(config.Read("postgres", "maxIdleConns"))
	errtools.CheckErr(err, "read maxIdleConns error")

	db, err := sql.Open("postgres", connStr)
	errtools.CheckErr(err, "postgres open error")

	dbConn = db

}

func query(sql string, args ...interface{}) {
	err := dbConn.Ping()
	errtools.CheckErr(err, "db ping error")

	dbConn.Query(sql, args)

}

func queryData() {
	connStr := "postgres://postgres:123456@localhost/mofy?sslmode=disable"
	db, err := sql.Open("postgres", connStr)
	if err != nil {
		log.Fatal(err)
	}

	db.SetMaxOpenConns(2000)
	db.SetMaxIdleConns(1000)

	if err := db.Ping(); err != nil {
		log.Fatal(err)
	}

	rows, err := db.Query("SELECT id,username FROM test ")

	for rows.Next() {
		var id int
		var username string

		if err := rows.Scan(&id, &username); err != nil {
			log.Fatal("scan error!")
		}

		fmt.Println(id, username)

	}

}
