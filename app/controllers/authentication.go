package controllers

import (
	"fmt"
	"github.com/dgrijalva/jwt-go"
	"github.com/duck1123/dinsro/app/models"
	"github.com/revel/revel"
	"golang.org/x/crypto/bcrypt"
	"log"
	"net/http"
	"time"
)

type Authentication struct {
	*revel.Controller
}

var hmacSecret = []byte{97, 48, 97, 50, 97, 98, 105, 49, 99, 102, 83, 53, 57, 98, 52,
	54, 97, 102, 99, 12, 12, 13, 56, 34, 23, 16, 78, 67, 54, 34, 32, 21}

func encodeToken(email string) (tokenString string, err error) {
	// Create a new token object, specifying signing method and the claims
	// you would like it to contain.
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"email": email,
		"nbf":   time.Date(2015, 10, 10, 12, 0, 0, 0, time.UTC).Unix(),
	})

	// Sign and get the complete encoded token as a string using the secret
	return token.SignedString(hmacSecret)
}

func decodeToken(tokenString string) (jwt.MapClaims, error) {
	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
		// Don't forget to validate the alg is what you expect:
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}

		// hmacSampleSecret is a []byte containing your secret, e.g. []byte("my_secret_key")
		return hmacSecret, nil
	})
	claims, ok := token.Claims.(jwt.MapClaims)
	if !(ok && token.Valid) {
		return nil, err
	}
	return claims, nil
}

func (c Authentication) Authenticate(authentication models.Authentication) revel.Result {
	log.Println("login")
	email := c.Params.Get("email")
	password := c.Params.Get("password")

	authenticationError := models.AuthenticationError{Message: "Authentication Failed"}

	user, err := GetUserService().GetByEmail(email)
	if err != nil {
		log.Println(err)
		c.Response.Status = http.StatusBadRequest
		return c.RenderJSON(authenticationError)
	}

	err = bcrypt.CompareHashAndPassword(user.PasswordHash, []byte(password))
	if err != nil {
		log.Println(err)
		c.Response.Status = http.StatusBadRequest
		return c.RenderJSON(authenticationError)
	}

	// get token
	tokenString, err := encodeToken(email)
	if err != nil {
		log.Println(err)
		c.Response.Status = http.StatusBadRequest
		return c.RenderJSON(authenticationError)
	}

	msg := make(map[string]string)
	msg["result"] = "login success"
	msg["token"] = tokenString
	c.Response.Status = http.StatusCreated
	return c.RenderJSON(msg)
}
