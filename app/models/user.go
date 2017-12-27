package models

import (
	"fmt"
	"github.com/revel/revel"
	"regexp"
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

func (user *User) String() string {
	return fmt.Sprintf("User(%s)", user.Email)
}

var emailRegexp = regexp.MustCompile(`[a-zA-Z0-9_\-]+@[a-zA-Z0-9_\-]+\.[a-zA-Z0-9_\-]+[a-zA-Z0-9]+$`)

func (user *User) Validate(v *revel.Validation) {
	ValidateEmail(v, user.Email).Key("user.Email")

	ValidatePassword(v, user.Password).
		Key("user.Password")

	v.Check(user.Name,
		revel.Required{},
		revel.MaxSize{Max: 100},
	)
}

func ValidatePassword(v *revel.Validation, password string) *revel.ValidationResult {
	return v.Check(password,
		revel.Required{},
		revel.MaxSize{Max: 15},
		revel.MinSize{Min: 5},
	)
}

func ValidateEmail(v *revel.Validation, email string) *revel.ValidationResult {
	return v.Check(email,
		revel.Required{},
		revel.Match{Regexp: emailRegexp},
	)
}
