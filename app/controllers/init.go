package controllers

import (
	"github.com/duck1123/dinsro/app/models"
	"github.com/duck1123/dinsro/app/services"
	"github.com/revel/modules/orm/gorp/app"
	"github.com/revel/revel"
	"net/http"
	"strings"
)

func GetUserService() services.UserService {
	return services.UserService{Db: gorp.Db}
}

func GetTransactionsService() services.TransactionService {
	return services.TransactionService{Db: gorp.Db}
}

func Authenticate(c *revel.Controller) revel.Result {
	tokenString, err := getTokenString(c)
	if err != nil {
		c.Response.Status = http.StatusBadRequest
		return c.RenderJSON(models.AuthenticationError{Message: "get token string failed"})
	}

	claims, err := decodeToken(tokenString)
	if err != nil {
		c.Response.Status = http.StatusUnauthorized
		return c.RenderJSON(models.AuthenticationError{Message: "auth failed"})
	}

	email, found := claims["email"]
	if !found {
		c.Response.Status = http.StatusBadRequest
		return c.RenderJSON(models.AuthenticationError{Message: "email not found in db"})
	}

	user, err := GetUserService().GetByEmail(email.(string))
	if err != nil {
		c.Response.Status = http.StatusUnauthorized
		return c.RenderJSON(models.AuthenticationError{Message: "auth failed"})
	}

	c.Args["userId"] = user.Id
	return nil
}

func getTokenString(c *revel.Controller) (tokenString string, err error) {
	authHeader := c.Request.Header.Get("Authorization")
	if authHeader == "" {
		return "", errAuthHeaderNotFound
	}

	tokenSlice := strings.Split(authHeader, " ")
	if len(tokenSlice) != 2 {
		return "", errInvalidTokenFormat
	}

	tokenString = tokenSlice[1]
	return tokenString, nil
}

func init() {
	revel.InterceptFunc(Authenticate, revel.BEFORE, &Transactions{})
	revel.InterceptFunc(Authenticate, revel.BEFORE, &Users{})
}
