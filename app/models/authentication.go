package models

type Authentication struct {
	Email    string `json:"email"`
	Password string `json:"password"`
}
