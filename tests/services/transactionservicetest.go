package services

import (
	"github.com/duck1123/dinsro/app/models"
	"github.com/duck1123/dinsro/app/services"
	"github.com/revel/modules/orm/gorp/app"
	"github.com/revel/revel/testing"
)

type TransactionServiceTest struct {
	testing.TestSuite
}

func (t *TransactionServiceTest) TestIndexWithEmptyTable() {
	service := services.TransactionService{Db: gorp.Db}

	// TODO: Replace with Service call
	gorp.Db.Map.TruncateTables()

	users, err := service.Index()
	t.Assertf(err == nil,
		"Error Occured - %s", err)

	t.Assertf(len(users) == 0,
		"Count of transactions wrong. Expected: 0. Got: %d",
		len(users))
}

func (t *TransactionServiceTest) TestIndexWithLoadedTable() {
	service := services.TransactionService{Db: gorp.Db}

	transaction := models.Transaction{}
	// TODO: Replace with Service call
	gorp.Db.Insert(&transaction)

	users, err := service.Index()
	t.Assertf(err == nil,
		"Error Occured - %s", err)
	t.Assertf(len(users) > 0,
		"Count of transactions wrong. Expected: >0. Got: %d",
		len(users))
}
