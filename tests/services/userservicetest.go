package services

import (
	"github.com/duck1123/dinsro/app/models"
	"github.com/duck1123/dinsro/app/services"
	"github.com/revel/modules/orm/gorp/app"
	"github.com/revel/revel/testing"
)

type UserServiceTest struct {
	testing.TestSuite
}

func (t *UserServiceTest) TestIndexWithEmptyTable() {
	service := services.UserService{Db: gorp.Db}

	// TODO: Replace with Service call
	gorp.Db.Map.TruncateTables()

	users, err := service.Index()
	t.Assertf(err == nil,
		"Error Occured - %s", err)

	t.Assertf(len(users) == 0,
		"Count of users wrong. Expected: 0. Got: %d",
		len(users))
}

func (t *UserServiceTest) TestIndexWithLoadedTable() {
	service := services.UserService{Db: gorp.Db}

	user := models.User{}
	// TODO: Replace with Service call
	gorp.Db.Insert(&user)

	users, err := service.Index()
	t.Assertf(err == nil,
		"Error Occured - %s", err)
	t.Assertf(len(users) > 0,
		"Count of users wrong. Expected: >0. Got: %d",
		len(users))
}
