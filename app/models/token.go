package models

import "time"

type Token struct {
	Id           uint32    `json:"id" db:"id"`
	AccessToken  string    `json:"accessToken" db:"accessToken"`
	RefreshToken string    `json:"refreshToken" db:"refreshToken"`
	Created      time.Time `json:"created" db:"created"`
	Updated      time.Time `json:"updated" db:"updated"`
}

func (Token) TableName() string {
	return "tokens"
}
