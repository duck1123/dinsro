package controllers

import (
	"github.com/duck1123/dinsro/app/models"
	"github.com/duck1123/dinsro/app/services"
	"github.com/revel/modules/orm/gorp/app/controllers"
	"github.com/revel/revel"
)

type Transactions struct {
	gorpController.Controller
}

func (c Transactions) getService() services.TransactionService {
	return services.TransactionService{Db: c.Db}
}

func (c Transactions) IndexApi() revel.Result {
	transactions, err := c.getService().Index()
	if err != nil {
		panic(err)
	}
	return c.RenderJSON(transactions)
}

func (c Transactions) ShowApi(id uint32) revel.Result {
	transaction, err := c.getService().Get(id)
	if err != nil {
		c.Response.Status = 404
		panic(err)
	}
	return c.RenderJSON(transaction)
}

func (c Transactions) CreateApi(transaction models.Transaction) revel.Result {
	c.getService().Create(&transaction)
	return c.RenderJSON(transaction)
}
