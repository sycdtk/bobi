# bobi
	支撑框架，整理和记录的通用功能。

#### config
	配置文件读写，采用key=value的方式进行保存配置，能够支持分组。logger使用此功能，例如：
	```
	[default]
	path= c:/go
	version = 1.44
	dsnName     = DSN=watch1
 
	[test]
	num =	666
	something  = wrong  #注释1
	#fdfdfd = fdfdfd    注释整行
	refer= refer       //注释3
	somet[hi]ng  = wrong  #注释1
	```
	
#### db
#### expression
	表达式计算，用以计算rpn（函数逆波兰）表达式的计算。例如：
	@AND(true,true,true)   返回结果true
	@NIN(ff,data)    data为map[string]string{"data":"11,22,aa,ff"}  返回结果为true 

#### logger
	日志输出工具，支持文件输出和console。日志级别分为：debug、info、error
	
#### md5
	字符串生成md5
	
#### random
	随机字符串生成工具，用以生成ID
	
#### rpn
	逆波兰函数表达式解析，采用函数的后缀表达式进行解析。例如:
	原始表达式：@OR(@SEQ(a,b),@SEQ(c,d),@IN(e,f))
	解析后表达式：| | a b @SEQ | c d @SEQ | e f @IN @OR
	
	目前空格 , | ( ) 五个符号作为分隔符，在表达式中不能使用。
	
#### session
#### set
	set工具
	
#### stack
	栈工具
	
#### workflow
	流程引擎