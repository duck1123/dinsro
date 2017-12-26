package services

import (
	"github.com/duck1123/dinsro/app/models"
	"github.com/revel/modules/orm/gorp/app"
	"time"
)

type TransactionService struct {
	Db *gorp.DbGorp
}

func (s TransactionService) Create(transaction *models.Transaction) error {
	modifyTime := time.Now()
	transaction.Created = modifyTime
	transaction.Updated = modifyTime
	return s.Db.Insert(transaction)
}

func (s TransactionService) Get(id uint32) (transaction models.Transaction, err error) {
	builder := s.Db.SqlStatementBuilder.Select("*").From(transaction.TableName()).Where("id = ?", id)
	err = s.Db.SelectOne(&transaction, builder)
	return transaction, err
}

func (s TransactionService) Index() (transactions []*models.Transaction, err error) {
	builder := s.Db.SqlStatementBuilder.Select("*").From("transactions")
	_, err = s.Db.Select(&transactions, builder)
	return transactions, err
}

func (s TransactionService) IndexByUser(id uint32) (transactions []*models.Transaction, err error) {
	builder := s.Db.SqlStatementBuilder.Select("*").From("transactions").Where("userId = ?", id)
	_, err = s.Db.Select(&transactions, builder)
	return transactions, err
}
