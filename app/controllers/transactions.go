package controllers

import (
	"github.com/duck1123/dinsro/app/services"
	"github.com/revel/modules/orm/gorp/app/controllers"
	"github.com/revel/revel"
)

type Transactions struct {
	gorpController.Controller
}

func (c Transactions) Index() revel.Result {
	service := services.TransactionService{Db: c.Db}
	transactions, err := service.Index()
	if err != nil {
		panic(err)
	}
	return c.Render(transactions)
}
