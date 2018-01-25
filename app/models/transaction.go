package models

import (
	"github.com/revel/revel"
	"time"
)

type Transaction struct {
	Id        uint32    `json:"id"         db:"id, primarykey, autoincrement"`
	AccountId uint32    `json:"accountId"  db:"accountId, notnull"`
	UserId    uint32    `json:"userId"     db:"userId, notnull"`
	Value     uint32    `json:"value"      db:"value, notnull"`
	Created   time.Time `json:"created"    db:"created, notnull"`
	Updated   time.Time `json:"updated"    db:"updated, notnull"`
}

func (Transaction) TableName() string {
	return "transactions"
}

func (transaction *Transaction) Validate(v *revel.Validation) {
	v.Required(transaction.Value)
	v.Min(int(transaction.AccountId), 1).Key("accountId")
	v.Min(int(transaction.UserId), 1).Key("userId")
	v.Min(int(transaction.Value), 1).Key("value")
}
