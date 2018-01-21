package models

import (
	"github.com/revel/revel"
	"time"
)

type Transaction struct {
	Id        uint32    `json:"id"         db:"id, primarykey, autoincrement"`
	AccountId uint32    `json:"accountId"  db:"accountId"`
	UserId    uint32    `json:"userId"     db:"userId"`
	Value     uint32    `json:"value"      db:"value"`
	Created   time.Time `json:"created"    db:"created"`
	Updated   time.Time `json:"updated"    db:"updated"`
}

func (Transaction) TableName() string {
	return "transactions"
}

func (transaction *Transaction) Validate(v *revel.Validation) {
	v.Required(transaction.Value)
}
