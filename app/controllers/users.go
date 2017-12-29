package controllers

import (
	"github.com/duck1123/dinsro/app/services"
	"github.com/revel/modules/orm/gorp/app/controllers"
	"github.com/revel/revel"
)

type Users struct {
	gorpController.Controller
}

func (c Users) getService() services.UserService {
	return services.UserService{Db: c.Db}
}

func (c Users) IndexApi() revel.Result {
	users, err := c.getService().Index()
	if err != nil {
		panic(err)
	}
	return c.RenderJSON(users)
}

func (c Users) ShowApi(id uint32) revel.Result {
	user, err := c.getService().Get(id)
	if err != nil {
		panic(err)
	}
	return c.RenderJSON(user)
}
