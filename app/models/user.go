package models

import (
	"time"
)

type User struct {
	Id           uint32    `json:"id" db:"id, primarykey, autoincrement"`
	Name         string    `json:"name" db:"name"`
	Email        string    `json:"email" db:"email"`
	PasswordHash []byte    `json:"-" db:"passwordHash"`
	Created      time.Time `json:"created" db:"created"`
	Updated      time.Time `json:"updated" db:"updated"`
}

func (User) TableName() string {
	return "users"
}
