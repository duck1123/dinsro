package controllers

import (
	"github.com/duck1123/dinsro/app/models"
	"github.com/revel/revel"
)

type Authentication struct {
	*revel.Controller
}

func (c Authentication) Authenticate(authentication models.Authentication) revel.Result {
	if authentication.Username != "demo" ||
		authentication.Password != "demo" {
		authenticationError := models.AuthenticationError{Message: "Authentication Failed"}
		c.Response.Status = 401
		return c.RenderJSON(authenticationError)
	}
	return c.RenderJSON(authentication)
}
