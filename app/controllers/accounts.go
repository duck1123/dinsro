package controllers

import (
	"github.com/duck1123/dinsro/app/models"
	"github.com/revel/modules/orm/gorp/app/controllers"
	"github.com/revel/revel"
)

type Accounts struct {
	gorpController.Controller
}

func (c Accounts) Create(account models.Account) revel.Result {
	account.OwnerId = c.Args["userId"].(uint32)
	GetAccountService().Create(&account)
	return c.RenderJSON(account)
}

func (c Accounts) Index() revel.Result {
	accounts, err := GetAccountService().Index()
	if err != nil {
		panic(err)
	}
	return c.RenderJSON(accounts)
}

func (c Accounts) IndexTransactions(id uint32) revel.Result {
	transactions, err := GetTransactionsService().IndexByAccount(id)
	if err != nil {
		panic(err)
	}
	return c.RenderJSON(transactions)
}

func (c Accounts) Show(id uint32) revel.Result {
	accounts, err := GetAccountService().Get(id)
	if err != nil {
		panic(err)
	}
	return c.RenderJSON(accounts)
}
