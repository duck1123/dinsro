package controllers

import (
	"github.com/duck1123/dinsro/app/models"
	"github.com/duck1123/dinsro/app/services"
	"github.com/revel/modules/orm/gorp/app/controllers"
	"github.com/revel/revel"
)

type Users struct {
	gorpController.Controller
}

func (c Users) Index() revel.Result {
	service := services.UserService{Db: c.Db}
	users, err := service.Index()
	if err != nil {
		panic(err)
	}
	return c.Render(users)
}

func (c Users) Show(id uint32) revel.Result {
	user := models.User{Id: 1}
	return c.Render(user)
}
