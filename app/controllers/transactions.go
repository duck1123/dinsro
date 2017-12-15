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

func (c Transactions) Index() revel.Result {
	transactions, err := c.getService().Index()
	if err != nil {
		panic(err)
	}
	return c.Render(transactions)
}

func (c Transactions) IndexApi() revel.Result {
	transactions, err := c.getService().Index()
	if err != nil {
		panic(err)
	}
	return c.RenderJSON(transactions)
}

func (c Transactions) Show(id uint32) revel.Result {
	transaction := models.Transaction{Id: 1}
	return c.Render(transaction)
}

func (c Transactions) ShowApi(id uint32) revel.Result {
	transaction := models.Transaction{Id: 1}
	return c.RenderJSON(transaction)
}

func (c Transactions) CreateApi(transaction models.Transaction) revel.Result {
	return c.RenderJSON(transaction)
}
