package services

import (
	"github.com/duck1123/dinsro/app/models"
	"github.com/revel/modules/orm/gorp/app"
	"github.com/revel/revel"
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

func (s TransactionService) Get(id uint32, updated *time.Time) (transaction models.Transaction, err error) {
	builder := s.Db.SqlStatementBuilder.Select("*").From(transaction.TableName()).Where("id = ?", id)
	if updated != nil {
		revel.AppLog.Info("Applying updated")
		builder.Where("updated < ?", updated)
	}
	err = s.Db.SelectOne(&transaction, builder)
	return transaction, err
}

func (s TransactionService) Index() (transactions []*models.Transaction, err error) {
	builder := s.Db.SqlStatementBuilder.
		Select("*").From("transactions").OrderBy("updated desc")

	_, err = s.Db.Select(&transactions, builder)
	return transactions, err
}

func (s TransactionService) IndexByAccount(id uint32) (transactions []*models.Transaction, err error) {
	builder := s.Db.SqlStatementBuilder.Select("*").
		From("transactions").
		Where("accountId = ?", id).
		OrderBy("updated desc")
	_, err = s.Db.Select(&transactions, builder)
	return transactions, err
}

func (s TransactionService) IndexByUser(id uint32) (transactions []*models.Transaction, err error) {
	builder := s.Db.SqlStatementBuilder.Select("*").
		From("transactions").
		Where("userId = ?", id).
		OrderBy("updated desc")
	_, err = s.Db.Select(&transactions, builder)
	return transactions, err
}
