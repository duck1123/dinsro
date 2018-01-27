package controllers

import (
	"github.com/duck1123/dinsro/app/interceptors"
	"github.com/duck1123/dinsro/app/models"
	"github.com/revel/modules/orm/gorp/app/controllers"
	"github.com/revel/revel"
	"net/http"
)

type Transactions struct {
	gorpController.Controller
	interceptors.AuthenticatedController
}

func (c Transactions) IndexApi() revel.Result {
	transactions, err := GetTransactionsService().Index()
	if err != nil {
		panic(err)
	}
	return c.RenderJSON(transactions)
}

func (c Transactions) ShowApi(id uint32) revel.Result {
	transaction, err := GetTransactionsService().Get(id)
	if err != nil {
		c.Response.Status = 404
		panic(err)
	}
	return c.RenderJSON(transaction)
}

func (c Transactions) CreateApi(transaction models.Transaction) revel.Result {
	transaction.UserId = c.Args["userId"].(uint32)
	transaction.Validate(c.Validation)
	c.Validation.Required(transaction.AccountId).Message("Account ID is required")

	if c.Validation.HasErrors() {
		c.Response.SetStatus(http.StatusBadRequest)
		return c.RenderText("oops")
	}

	GetTransactionsService().Create(&transaction)
	return c.RenderJSON(transaction)
}
