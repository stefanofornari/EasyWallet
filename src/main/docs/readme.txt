EasyWallet
==========

A simple Crypto wallets app to easily manage and create personal wallets.

EasyWallet configuraton
-----------------------

EasyWallet reads and updates the configuration file
    $(HOME)/.config/ste.w3.easywallet/preferences.json

The content looks like the following:

---
{
  "endpoint":"https://the.network.endpoint",
  "appkey":"the endpoint app key",
  "wallets":[
  {
    "address":"the wallet address",
    "privateKey":"the private key if any",
    "mnemonicPhrase":"the mnemonic phrase to generate the keys if any",
    "balances":
    {
      "ETH":big decimal amount,
      "STORJ":big decimal amount
      ...
    }
  },
  ...
  ],
  "coins":[
  {
    "contract":"the coin contract, null if main for the network",
    "symbol":"the coin symbol, e.g. STORJ",
    "name":"the coin name, e.g. StorjToken",
    "decimals":number of decimals, e.g. 8
  },
  ...
  ],
  "db":"the jdbc connection string"
}
---

EasyWallet database
-------------------

EasyWallet should be compatible with any JDBC database, but it packages the
HyperSQL JDBC driver out of the box.

HyperSQL is very light and can be used for a fully standalone installation:

jdbc:hsqldb:file:./data/easywallet.db;shutdown=true

For more sofisticated installation, HyperSQL can be started as a server (see
HyperSQL server):

jdbc:hsqldb:hsql://hostname/easywallet;shutdown=true

Stay updated with the chain
---------------------------

A block chain network continuously generates new blocks with new transactions.

To retrieve the latest blocks from the UI press the REFRESH button. This will
update the balance of the monitored tokens and download the new available block
since the last block loaded. Note that the first time it is pressed only the
latest available block will be stored in EasyWallet database.

Do not be concerned if the download process takes time, if the number of blocks
to download are many, the process may take a lot of time, potentially several
days. Plus it is not unusual the process is interrupted by an error on the
endpoint side. Although pressing REFRESH again will recover from the last block
loaded. However, this requires a manual action, which is not practical or
advisable.

To address this problem, EasyWallet provides a command line tool that works
exactly like pressing REFRESH: when it starts, it downloads all blocks since the
last imported (or simply the LATEST block if run for the first time) and then
exits. The command can then be scheduled to restart regularly in the background
keeping EasyWallet's database up to date with the network.

This command is bin/ewloader.


EasyWallet Loader as a systemd service
--------------------------------------

An easy way to run ewloader so that it is automatically rescheduled (maybe after
a while) once done (regardless if it ends its job or it encountered an error),
is letting systemd do all the job.

Copy (or make a link) bin/ewloader.service under /lib/systemd/system/ and edit
it to make sure it matches your installation. The default content of ewloader.service
looks like:

---
[Unit]
Description=EasyWallet Loader

[Service]
ExecStart=/opt/EasyWallet/bin/ewloader
Restart=always
RestartSec=15m
User=easywallet

[Install]
WantedBy=multi-user.target
---

Note the two directives 'Restart=always' and 'RestartSec=15m' make the trick:

- the command is reinvoked any time it exists
- but waiting 15m

The 15 minutes delay is to avoid to overload the network (or the system in case
of multiple errors).

To install the service:
    sudo systemctl daemon-reload

To start the service:
    sudo systemctl start ewloader

To stop the service:
    sudo systemctl stop ewloader

To check the status of the service:
    sudo systemctl status ewloader


HyperSQL server
---------------
Similarly to EaseWallet Loader, HyperSQL can be started as a systemd service.

Copy (or make a link) bin/ewdb.service under /lib/systemd/system/ and edit
it to make sure it matches your installation. The default content of ewdb.service
looks like:

---
[Unit]
Description=EasyWallet HyperSQL DB

[Service]
ExecStart=/opt/EasyWallet/bin/hsqldbserver
User=easywallet

[Install]
WantedBy=multi-user.target
---

To install the service:
    sudo systemctl daemon-reload

To start the service:
    sudo systemctl start ewdb

To stop the service:
    sudo systemctl stop ewdb

To check the status of the service:
    sudo systemctl status ewdb
