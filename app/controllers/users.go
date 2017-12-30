package controllers

import (
	"github.com/revel/modules/orm/gorp/app/controllers"
	"github.com/revel/revel"
)

type Users struct {
	gorpController.Controller
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
