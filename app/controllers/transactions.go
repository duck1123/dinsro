package controllers

import (
	"github.com/duck1123/dinsro/app/interceptors"
	"github.com/duck1123/dinsro/app/models"
	"github.com/revel/modules/orm/gorp/app/controllers"
	"github.com/revel/revel"
	"net/http"
	"time"
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
	header := c.Request.Header.Get("If-Modified-Since")

	t, err := time.Parse(time.RFC1123, header)

	if err != nil {
		panic(err)
	}

	println("time " + t.String())

	transaction, err := GetTransactionsService().Get(id, &t)
	if err != nil {
		c.Response.Status = 404
		panic(err)
	}
	return c.RenderJSON(transaction)
}

func (c Transactions) CreateApi(transaction models.Transaction) revel.Result {
	transaction.UserId = c.Args["userId"].(uint32)
	transaction.Validate(c.Validation)

	if c.Validation.HasErrors() {
		c.Response.SetStatus(http.StatusBadRequest)
		return c.RenderJSON(c.Validation.Errors)
	}

	err := GetTransactionsService().Create(&transaction)

	if err != nil {
		panic(err)
	}

	return c.RenderJSON(transaction)
}
