package controllers

import (
	"github.com/duck1123/dinsro/app/models"
	"github.com/revel/modules/orm/gorp/app/controllers"
	"github.com/revel/revel"
	"golang.org/x/crypto/bcrypt"
	"log"
	"net/http"
)

type Users struct {
	gorpController.Controller
}

// Register create a user and returns token to client.
// params: email, password
// result: token with user.id stores in `sub` field.
func (c Users) Register() revel.Result {
	// create user use, email, password
	// return token to user
	email := c.Params.Get("email")
	password := c.Params.Get("password")

	//
	if email == "" || password == "" {
		// this is not json
		c.Response.Status = http.StatusBadRequest
		return c.RenderJSON("params is not valid.")
	}

	// check if the email have already exists in DB
	user, err := GetUserService().GetByEmail(email)
	if err != nil {
		log.Println(err)
	}
	if user != nil {
		c.Response.Status = http.StatusConflict
		return c.RenderJSON("user already exists.")
	}

	// Crete user struct
	bcryptPassword, _ := bcrypt.GenerateFromPassword(
		[]byte(password), bcrypt.DefaultCost)

	token, err := encodeToken(email)
	if err != nil {
		log.Println(err)
	}

	newUser := &models.User{Name: "Demo User",
		Email:          email,
		PasswordHash: bcryptPassword,
		Token:          []byte(token)}

	// Validate user struct
	newUser.Validate(c.Validation)
	if c.Validation.HasErrors() {
		log.Println(c.Validation.Errors[0].Message)
		c.Response.Status = http.StatusBadRequest
		return c.RenderJSON("bad email address.")
	}

	// Save user info to DB
	if err := c.Db.Insert(newUser); err != nil {
		panic(err)
	}

	msg := make(map[string]string)
	msg["email"] = email
	msg["result"] = "user created"
	msg["token"] = token
	return c.RenderJSON(msg)
}

func (c Users) IndexApi() revel.Result {
	users, err := GetUserService().Index()
	if err != nil {
		panic(err)
	}
	return c.RenderJSON(users)
}

func (c Users) ShowApi(id uint32) revel.Result {
	user, err := GetUserService().Get(id)
	if err != nil {
		panic(err)
	}
	return c.RenderJSON(user)
}

