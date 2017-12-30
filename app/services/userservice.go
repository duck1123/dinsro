package services

import (
	"github.com/duck1123/dinsro/app/models"
	"github.com/revel/modules/orm/gorp/app"
)

type UserService struct {
	Db *gorp.DbGorp
}

func (s UserService) Get(id uint32) (user models.User, err error) {
	builder := s.Db.SqlStatementBuilder.Select("*").From(user.TableName()).Where("id = ?", id)
	err = s.Db.SelectOne(&user, builder)
	return user, err
}

func (s UserService) Index() (users []*models.User, err error) {
	builder := s.Db.SqlStatementBuilder.Select("*").From("users")
	_, err = s.Db.Select(&users, builder)
	return users, err
}

func (s UserService) GetByEmail(email string) (user *models.User, err error) {
	builder := s.Db.SqlStatementBuilder.
		Select("*").
		From("users"). // TODO: extract this
		Where("email = ?", email)
	err = s.Db.SelectOne(&user, builder)
	if err != nil {
		return nil, err
	}
	return user, nil
}
