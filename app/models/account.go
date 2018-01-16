package models

type Account struct {
	Id      uint32 `json:"id" db:"id"`
	Name    string `json:"name" db:"name"`
	OwnerId uint32 `json:"ownerId" db:"ownerId"`
}
