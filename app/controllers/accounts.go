package controllers

import (
	"github.com/revel/modules/orm/gorp/app/controllers"
	"github.com/revel/revel"
)

type Accounts struct {
	gorpController.Controller
}

func (c Accounts) Index() revel.Result {
	accounts, err := GetAccountService().Index()
	if err != nil {
		panic(err)
	}
	return c.RenderJSON(accounts)
}

func (c Users) Show(id uint32) revel.Result {
	accounts, err := GetAccountService().Get(id)
	if err != nil {
		panic(err)
	}
	return c.RenderJSON(accounts)
}
