package interceptors

import (
	"github.com/revel/modules/orm/gorp/app/controllers"
	"github.com/revel/revel"
)

type AuthenticatedController struct {
	gorpController.Controller
 }

func (c AuthenticatedController) Before() revel.Result {
	revel.AppLog.Info("Before hook")
	return nil
}

