package controllers

import (
	"github.com/duck1123/dinsro/app/models"
	"github.com/revel/modules/orm/gorp/app/controllers"
	"github.com/revel/revel"
)

type Users struct {
	gorpController.Controller
}

func (c Users) Index() revel.Result {
	var users []*models.User
	builder := c.Db.SqlStatementBuilder
	sb := builder.Select("*").From("users")
	_, err := c.Db.Select(&users, sb)
	if err != nil {
		panic(err)
	}
	return c.Render(users)
}
