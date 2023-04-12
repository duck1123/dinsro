ci-local:
	bb ci-local

clean:
	bb clean

deploy:
	devspace run deploy-all

dev:
	bb dev

format:
	bb format

lint:
	bb lint
