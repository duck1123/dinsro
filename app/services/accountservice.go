package services

import (
	"github.com/duck1123/dinsro/app/models"
	"github.com/revel/modules/orm/gorp/app"
	"time"
)

type AccountService struct {
	Db *gorp.DbGorp
}

func (s AccountService) Create(account *models.Account) error {
	modifyTime := time.Now()
	account.Created = modifyTime
	account.Updated = modifyTime
	return s.Db.Insert(account)
}

func (s AccountService) Get(id uint32) (account *models.Account, err error) {
	builder := s.Db.SqlStatementBuilder.
		Select("*").
		From("accounts").
		Where("id = ?", id)
	err = s.Db.SelectOne(&account, builder)
	return account, err
}

func (s AccountService) Index() (accounts []*models.Account, err error) {
	builder := s.Db.SqlStatementBuilder.
		Select("*").
		From("accounts")
	_, err = s.Db.Select(&accounts, builder)
	return accounts, err
}

func (s AccountService) GetByEmail(email string) (account *models.Account, err error) {
	builder := s.Db.SqlStatementBuilder.
		Select("*").
		From("accounts"). // TODO: extract this
		Where("email = ?", email)
	err = s.Db.SelectOne(&account, builder)
	if err != nil {
		return nil, err
	}
	return account, nil
}
