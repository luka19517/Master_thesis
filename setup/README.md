# Uputstvo za pripremu postgres i hbase okruzenja

Komande izvrsavati u git bash-u

Pokretanje postgres-a i hbase-a

	./start-databases.sh

Zaustavlanje postgres-a i hbase-a

	./stop-databases.sh

Za pokretanje psql wrapper-a sa lokalne masine:

	sh ./psqlWrapper.sh

Komanda koja se unese ce biti izvrsena na docker kontejneru


# Populacija podataka

Tabela FXRates sadrzi za svaki par valuta (EUR,USD,DIN,CHF), sadrzi dva sloga za kurs.

Konvencije vezane za populaciju podataka

n - redni broj iteracije (pocevsi od 0)

##	FXUser (0<=n<30000)

username: username_n

password: password_n

startBalanceCurrencyCode: EUR (ako je n%4 = 0 ), USD (ako je n%4=1), DIN (ako je n%4=2), CHF (ako je n%4=3)

startBalance: n%10*10000  + n%9*1000 + n%8*100 + n%7*10 + n%6

firstName: firstName_n

lastName: lastName_n

street: street_(n%100)

city: city_(n%50)

state: state_(n%10)

zip: zip_(n%100)

phone: phone_n

mobile: mobile_n

memail: email_n

created: now()

## FXAccount (0<=n<90000)

fxUser: username_(n%30000)

currency_code: EUR (ako je n%4 = 0 ), USD (ako je n%4=1), DIN (ako je n%4=2), CHF (ako je n%4=3)

balance: n%10*10000  + n%9*1000 + n%8*100 + n%7*10 + n%6

created: now() 

## FXTransaction (0<=n<30000)

fxAccount_from: username_(n%20000): currency_code(EUR (ako je n%4 = 0 ), USD (ako je n%4=1), DIN (ako je n%4=2), CHF (ako je n%4=3))

fxAccount_to: username_(n%20000): currency_code(EUR (ako je n%4 = 1 ), USD (ako je n%4=2), DIN (ako je n%4=3), CHF (ako je n%4=0))

amount:  n%10*1000  + n%9*100 + n%8*10 + n%7 

status: SUCCESS

entryDate: now()
