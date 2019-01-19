package ddm

var dataDefinedMap = map[string]*dataDefined{} //数据模型定义集合，对所有datamodel为只读属性

type dataDefined struct {
	dataName     string               //结构名称
	fieldDefined map[string][2]string //属性名称,属性类型
}

//注册数据类型
func RegisterDataDefined(dataName string, fieldNames [][2]string) {
	if _, contain := dataDefinedMap[dataName]; !contain {

		fd := make(map[string][2]string) //对象属性集合

		for _, field := range fieldNames {
			fd[field[0]] = field
		}

		dataDefinedMap[dataName] = &dataDefined{dataName: dataName, fieldDefined: fd}
	}
}

//动态数据结构模型
type DataModel struct {
	*dataDefined                        //数据模型定义
	fields       map[string]interface{} //属性字段
}

//存值，字段不存在的情况忽略存入数据
func (data *DataModel) Set(field string, value interface{}) {
	if _, contain := data.dataDefined.fieldDefined[field]; contain {
		data.fields[field] = value
	}
}

//获取对象数据，若字段未定义，返回nil
func (data *DataModel) Get(field string) interface{} {
	if _, contain := data.dataDefined.fieldDefined[field]; contain {
		return data.fields[field]
	}
	return nil
}

//获取字符串类型的属性值
func (data *DataModel) GetString(field string) string {
	d := data.Get(field)
	if d != nil {
		if v, ok := d.(string); ok {
			return v
		}
	}
	return ""
}

//获取浮点类型的属性值
func (data *DataModel) GetFloat64(field string) float64 {
	d := data.Get(field)
	if d != nil {
		if v, ok := d.(float64); ok {
			return v
		}
	}
	return float64(0)
}

//新建数据对象
func NewDataModel(dataName string) *DataModel {
	if dd, contain := dataDefinedMap[dataName]; contain {
		return &DataModel{dataDefined: dd, fields: make(map[string]interface{})}
	}
	return nil
}

////存值，字段不存在的情况忽略存入数据
//func Set(data *DataModel, field string, value interface{}) {
//	if _, contain := data.dataDefined.fieldDefined[field]; contain {
//		data.fields[field] = value
//	}
//}

////获取对象数据，若字段未定义，返回nil
//func Get(data *DataModel, field string) interface{} {
//	if _, contain := data.dataDefined.fieldDefined[field]; contain {
//		return data.fields[field]
//	}
//	return nil
//}

////获取字符串类型的属性值
//func GetString(data *DataModel, field string) string {
//	d := Get(data, field)
//	if d != nil {
//		if v, ok := d.(string); ok {
//			return v
//		}
//	}
//	return ""
//}

////获取浮点类型的属性值
//func GetFloat64(data *DataModel, field string) float64 {

//	if d := Get(data, field); d != nil {
//		if v, ok := d.(float64); ok {
//			return v
//		}
//	}
//	return float64(0)
//}
