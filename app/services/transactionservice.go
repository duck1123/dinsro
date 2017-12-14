package services

import (
	"github.com/duck1123/dinsro/app/models"
	"github.com/revel/modules/orm/gorp/app"
)

type TransactionService struct {
	Db *gorp.DbGorp
}

func (s TransactionService) Create(transaction *models.Transaction) (id uint32, err error) {
	err = s.Db.Insert(transaction)

	if err != nil {
		return 0, err
	}

	return transaction.Id, err
}

func (s TransactionService) Index() (transactions []*models.Transaction, err error) {
	builder := s.Db.SqlStatementBuilder.Select("*").From("transactions")
	_, err = s.Db.Select(&transactions, builder)
	return transactions, err
}
