package models

import (
	"time"
)

type Account struct {
	Id      uint32    `json:"id"      db:"id, primarykey, autoincrement"`
	Name    string    `json:"name"    db:"name, notnull"`
	OwnerId uint32    `json:"ownerId" db:"ownerId, notnull"`
	Created time.Time `json:"created" db:"created, notnull"`
	Updated time.Time `json:"updated" db:"updated, notnull"`
}
