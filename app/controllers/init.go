package controllers

import (
	"github.com/duck1123/dinsro/app/services"
	"github.com/revel/modules/orm/gorp/app"
)

func GetUserService() services.UserService {
	return services.UserService{Db: gorp.Db}
}

func GetTransactionsService() services.TransactionService {
	return services.TransactionService{Db: gorp.Db}
}
