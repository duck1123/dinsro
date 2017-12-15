package models

type User struct {
	Id   uint32 `json:"id" xml:"id" db:"id, primarykey, autoincrement"`
	Name string `json:"name" xml:"name" db:"name"`
}

func (User) TableName() string {
	return "users"
}
