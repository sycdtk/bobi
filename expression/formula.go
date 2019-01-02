package exprssion

//公式模板
var formulas = map[string]string{}

//加载公式模板
func init() {
	//CAC – 08CVCONSTRAINED Percent of CVs at active constraint
	Reg("CAC", "@MUL(@DIV(@IF(@AND(@NE(DEPACT,0),@OR(@EQ(DEPSTA,0),@EQ(DEPSTA,2),@EQ(DEPSTA,3))),1,0),IPNDEP),100)")

	//CCS - 16CVATCONS Number of CVs at high / low limit, setpoint, ramp or external target
	Reg("CCS", "@OR(@EQ(DEPACT,1),@EQ(DEPACT,2),@EQ(DEPACT,7),@EQ(DEPACT,9),@EQ(DEPACT,20))")

	//CET - 14PCTCVETON Percent of CV external targets (ETs) in service
	Reg("CET", "@MUL(@DIV(@EQ(ETCSRV,1),IPNDEP),100)")

}
