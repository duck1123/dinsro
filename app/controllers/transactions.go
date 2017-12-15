package controllers

import (
	"github.com/revel/modules/orm/gorp/app/controllers"
	"github.com/revel/revel"
)

type Transactions struct {
	gorpController.Controller
}

func (c Transactions) Index() revel.Result {

	return c.Render()
}
