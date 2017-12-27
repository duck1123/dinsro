package controllers

import (
	"fmt"
	"github.com/dgrijalva/jwt-go"
	"github.com/duck1123/dinsro/app/models"
	"github.com/duck1123/dinsro/app/services"
	"github.com/revel/modules/orm/gorp/app/controllers"
	"github.com/revel/revel"
	"golang.org/x/crypto/bcrypt"
	"log"
	"net/http"
	"time"
)

var hmacSecret = []byte{97, 48, 97, 50, 97, 98, 105, 49, 99, 102, 83, 53, 57, 98, 52, 54, 97, 102, 99, 12, 12, 13, 56, 34, 23, 16, 78, 67, 54, 34, 32, 21}

type Users struct {
	gorpController.Controller
}

func (c Users) getService() services.UserService {
	return services.UserService{Db: c.Db}
}

func (c Users) getTransactionsService() services.TransactionService {
	return services.TransactionService{Db: c.Db}
}

// Register create a user and returns token to client.
// params: email, password
// result: token with user.id stores in `sub` field.
func (c Users) Register() revel.Result {
	// create user use, email, password
	// return token to user
	email := c.Params.Get("email")
	password := c.Params.Get("password")

	if email == "" || password == "" {
		c.Response.Status = http.StatusBadRequest
		return c.RenderJSON("params is not valid.")
	}

	// check if the email have already exists in DB
	user, err := c.getService().GetByEmail(email)
	if err != nil {
		log.Println(err)
	}
	if user != nil {
		c.Response.Status = http.StatusConflict
		return c.RenderJSON("user already exists.")
	}

	// Crete user struct
	bcryptPassword, _ := bcrypt.GenerateFromPassword(
		[]byte(password), bcrypt.DefaultCost)

	token := encodeToken(email)

	newUser := &models.User{Name: "Demo User",
		Email:          email,
		Password:       password,
		HashedPassword: bcryptPassword,
		Token:          []byte(token)}

	// Validate user struct
	newUser.Validate(c.Validation)
	if c.Validation.HasErrors() {
		log.Println(c.Validation.Errors[0].Message)
		c.Response.Status = http.StatusBadRequest
		return c.RenderJSON("bad email address.")
	}

	// Save user info to DB
	if err := Dbm.Insert(newUser); err != nil {
		panic(err)
	}

	msg := make(map[string]string)
	msg["email"] = email
	msg["result"] = "user created"
	msg["token"] = token
	return c.RenderJSON(msg)
}

// Login authticate via email and password, if the user is valid,
// returns the token to client.
func (c Users) Login() revel.Result {
	email := c.Params.Get("email")
	password := c.Params.Get("password")

	user, err := c.getService().GetByEmail(email)
	if err != nil {
		log.Println(err)
		c.Response.Status = http.StatusBadRequest
		return c.RenderJSON("invalid email or password")
	}

	err = bcrypt.CompareHashAndPassword(user.HashedPassword, []byte(password))
	if err != nil {
		log.Println(err)
		c.Response.Status = http.StatusBadRequest
		return c.RenderJSON("invalid email or password")
	}

	// get token
	tokenString := encodeToken(email)

	msg := make(map[string]string)
	msg["result"] = "login success"
	msg["token"] = tokenString
	c.Response.Status = http.StatusCreated
	return c.RenderJSON(msg)
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

func (c Users) IndexTransactions(id uint32) revel.Result {
	transactions, err := c.getTransactionsService().IndexByUser(id)
	if err != nil {
		panic(err)
	}
	return c.RenderJSON(transactions)
}

func encodeToken(email string) string {
	// Create a new token object, specifying signing method and the claims
	// you would like it to contain.
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"email": email,
		"nbf":   time.Date(2015, 10, 10, 12, 0, 0, 0, time.UTC).Unix(),
	})

	// Sign and get the complete encoded token as a string using the secret
	tokenString, err := token.SignedString(hmacSecret)

	fmt.Println(tokenString, err)

	return tokenString
}

func decodeToken(tokenString string) (jwt.MapClaims, error) {
	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
		// Don't forget to validate the alg is what you expect:
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("Unexpected signing method: %v", token.Header["alg"])
		}

		// hmacSampleSecret is a []byte containing your secret, e.g. []byte("my_secret_key")
		return hmacSecret, nil
	})
	claims, ok := token.Claims.(jwt.MapClaims)
	if ok && token.Valid {
		fmt.Println("email and nbf:", claims["email"], claims["nbf"])
	} else {
		log.Println(err)
		return nil, err
	}
	return claims, nil
	// return claims["email"].(string), claims["nbf"].(string)
}
