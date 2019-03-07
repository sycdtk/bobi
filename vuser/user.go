package vuser

import (
	"github.com/sycdtk/bobi/md5"
	"github.com/sycdtk/bobi/random"
	"github.com/sycdtk/bobi/set"
)

const (
	Normal    = 1001 //用户正常状态
	Vacation  = 1002 //休假状态
	Dimission = 1003 //用户离职，用户工单信息可见
	Closed    = 1009 //用户注销，用户工单信息不可见

	Male   = "m" //男
	Female = "f" //女
	Unknow = "u" //未知
)

//用户信息
type User struct {
	ID          string
	Name        string
	Description string

	Gender string //性别

	Username string
	Password string

	Email     string //邮件地址
	Telephone string //座机电话
	Mobile    string //手机

	Logining bool //登录状态，true：登录中，false：未登录
	Locked   bool //用户锁定，true：用户被锁定，false：未被锁定
	ErrorNum int  //错误登录次数
	Status   int  //正常、休假、离职、注销

	StandbyUser string //用户ID，用户B岗位，仅支持用户，仅针对休假状态生效，仅支持一级，若B岗人员也休假，则不生效

	Groups        *set.Set //用户所属用户组ID集合
	Organizations *set.Set //用户所属组织机构ID集合
}

func NewUser(name, description, gender, username, password, email, telephone, mobile, standbyUser string, groups, organizations *set.Set) *User {
	user := &User{
		ID:            random.UniqueID(),
		Name:          name,
		Gender:        gender,
		Description:   description,
		Username:      username,
		Password:      md5.MD5(password),
		Email:         email,
		Telephone:     telephone,
		Mobile:        mobile,
		StandbyUser:   standbyUser,
		Groups:        groups,
		Organizations: organizations,
		Logining:      false,
		Locked:        false,
		ErrorNum:      0,
		Status:        Normal,
	}

	//加入缓存
	Manager.users[user.ID] = user

	//更新group
	if !groups.Empty() {
		for _, groupID := range groups.Array() {
			if group, ok := Manager.groups[groupID]; ok {
				group.Users.Add(user.ID)
			}
		}
	}

	//更新organization
	if !organizations.Empty() {
		for _, organizationID := range organizations.Array() {
			if organization, ok := Manager.organizations[organizationID]; ok {
				organization.Members.Add(user.ID)
			}
		}
	}

	return user
}
