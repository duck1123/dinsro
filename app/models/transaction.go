package models

import "time"

type Transaction struct {
	Id      uint32    `json:"id"      db:"id, primarykey, autoincrement"`
	UserId  uint32    `json:"userId"  db:"userId"`
	Value   uint32    `json:"value"   db:"value"`
	Created time.Time `json:"created" db:"created"`
	Updated time.Time `json:"updated" db:"updated"`
}

func (Transaction) TableName() string {
	return "transactions"
}
