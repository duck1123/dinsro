package models

import (
	"time"
)

type Account struct {
	Id      uint32    `json:"id" db:"id, primarykey, autoincrement"`
	Name    string    `json:"name" db:"name"`
	OwnerId uint32    `json:"ownerId" db:"ownerId"`
	Created time.Time `json:"created" db:"created"`
	Updated time.Time `json:"updated" db:"updated"`
}
