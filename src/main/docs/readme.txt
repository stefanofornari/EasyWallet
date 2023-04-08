EasyWallet
==========

A simple Crypto wallets app to easily manage and create personal wallets.

EasyWallet reads and updates the configuration file $(HOME)/.config/ste.w3.easywallet/preferences.json

The content looks like the following:

```
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
  ]
}
```
