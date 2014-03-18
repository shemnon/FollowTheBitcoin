/*
 * Follow the  Bitcoin
 * Copyright (C) 2014  Danno Ferrin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package com.shemnon.btc.ftm

import com.shemnon.btc.coinbase.CBAddress
import com.shemnon.btc.coinbase.CBTransaction
import com.shemnon.btc.model.IBase
import javafx.scene.control.TreeItem

/**
 * Created by shemnon on 8 Mar 2014.
 */
class FamousEntries {

    static TreeItem<IBase> createFamousTree() {
        TreeItem<JsonBase> addresses = new TreeItem<>(new JsonBaseLabel("Addresses (50 newest tx)"))
        addresses.children.addAll(
                new TreeItem<>(new CBAddress([
                        label: 'Dorian Donations',
                        address: '1Dorian4RoXcnBv9hnQ4Y2C1an6NJ4UrjX',
                        created_at: ''
                ])),
                new TreeItem<>(new CBAddress([
                        label: 'ESPN \'HiMom\' Sign Guy',
                        address: '1HiMoMgBaAikFHgAt3M4YJtetp4HrnsiXu',
                        created_at: ''
                ])),
                new TreeItem<>(new CBAddress([
                        label: 'Silk Road Seized Coins',
                        address: '1F1tAaz5x1HUXrCNLbtMDqcw6o5GNn4xqX',
                        created_at: ''
                ])),
                new TreeItem<>(new CBAddress([
                        label: 'CryptoCrumb Tip Jar',
                        address: '1CRUMB7EPfZjVPqML1Wk4q8WJdusLufsVr',
                        created_at: ''
                ]))
        )
        //Other tip jars?
        

        TreeItem<JsonBase> transactions = new TreeItem<>(new JsonBaseLabel("Transactions"))
        transactions.children.addAll(
                new TreeItem<>(new CBTransaction(
                        [ notes: 'MtGox 424242.42424242',
                        amount:[amount:'424242.42424242', currency:'BTC'], 
                        created_at: '2011-06-23T06:50:15+00:00', 
                        hsh:'3a1b9e330d32fef1ee42f8e86420d2be978bbe0dc5862f17da9027cf9e11f8c4'])
                ),
                new TreeItem<>(new CBTransaction(
                        [notes: 'MtGox 180K Binary Split',
                        amount:[amount:'180000', currency:'BTC'], 
                        created_at: '2014-03-07T07:24:02+00:00', 
                        hsh:'1dda0f8827518ce4d1d824bf7600f75ec7e199774a090a947c58a65ab63552e3'])
                ),
                new TreeItem<>(new CBTransaction(
                        [notes: 'MtGox 19K Binary Split',
                        amount:[amount:'19999.99', currency:'BTC'], 
                        created_at: '2014-03-07T07:29:02+00:00', 
                        hsh:'a4b08370f09186eadcc415fe1a3b0d55e74a9eab9400a5a9fdb4031a6d7eba18'])
                ),
        )
        // other heists?

        TreeItem<JsonBase> rootLabel = new TreeItem<>(new JsonBaseLabel("Famous Entries"))
        rootLabel.children.addAll(addresses, transactions)
        return rootLabel
    }
    
}
/*
{"balanceSat":1088690663,"totalReceivedSat":4422900663,"totalSentSat":3334210000,"unconfirmedBalanceSat":0,"txApperances":130,"unconfirmedTxApperances":0,"transactions":
["8c2d053567077f72027032bf9c1d909bda015954e2095518a18cfd5e60ccccb0",
"1b0136aeace723c75ef582ab0242540dc7d8814efa4150b74f80e649149a251e",
"aa7d778ed1d1b2e1673ce93a83f1ed432bd312c1851219b53bd80fce3da3e241",
"ad7b0eaac4624c5b471dbb4452a313d0d22bdfcd10bb508e5f9ed4b70afa446a",
"984a6e61b75abb3b1403619a579399003cb7078b5b3f279d52540d1b62287eb1",
"bd3b946e77a6b21d1d34f90d756d23d81f60def32c33fffe977874aa1c86c8f5",
"f70d3f9c3518fd991f45f55e9cffd8c58d33dcb897e8e2ac6baff018fbbed5ca",
"45b5244147ba99e6c91f493ddcace59709d9ca01d591ec61e7c94ed0e5e10933",
"b9eb51c5debeb862bfda293476e88c103c48266516d73c9d2c2faa8909900489",
"3adab2c721f5f734ff8f68a1938014b75bfabf6a7a3ed902f0b9600f3be1ba8e",
"4cf3d96a661daa9e33bc57a3aaefaa1dcefd40915df4001f0ea31bde6e93f8fb",
"bb0785be985b4dcd50fa4d61d57193294d3c0219dbce3b68b7a89ca7a82d2673",
"45efea2bad51e0bc202de3874b8dd46d13eaffdff087ef1a8b7535661fd6767f",
"b6f4ec453a021ac561b01039f78e7168a653af176353c86d607343cc77e779b9",
"386706c652a80b83f915daa59b5edd2ea7c11b98277910d72b5e04b3a5784efc",
"b5f3fdd5d48ad14d1a4d6f5cd11f9afb9832cfe29e2cfbefaa08438d57fc014d",
"8e98ff2a36a82a7d6c0f7257969f107837e0baf4c031e098ae0442ec52e0f882",
"bfae5310d3602c30d0465746e5cf0758090aab96f7b75dec57d7c063af8c1e3f",
"f8b2400df14ba01005dd2193a10b378439543cfaa28f60ea3861caf0b2805150",
"df457b176f9a1a9d6876f75302f1a5b22052b9238ce330bdfeecca401ca8f396",
"3ace1681850948832ed14a504b3171422b1d2bcdc955d0f4d8bf363d52dcdbfa",
"c680ea711118d62075f63df1b9216cc62b5d6e309e2c97e5fbc6b0b91b2fb48b",
"2cf5969e69679bf616f44b6876fc8ed3dcaca7fd9458fb7433cd5f09a04f62df",
"33dbc00df344260a3166c2edd34fedf50eb122d254a02ca1afdd882ab9053a38",
"c60f933fd7a5bebae7d9acc2d8cdb7e9dfc80c17be873807b9f2d6883f24b39c",
"62326c00bb9e274fb3a5dadc444a88efe06ddafcc5d4e74efd3c36fd8f97f5d1",
"a7f20b96923517414dc9fdf3f315e5ea957978940dd6234d90a3f66071f88f31",
"5ac02e83761f2ab0b0c6d3f3e8e061952dd14665eb71433354bd8a3e4e0bdbb4",
"c008a467aab185b01be8fab0827026a26957f301a73391aff5e372ab01c1ca33",
"3cc0bc0d228ee2e3dc0a0ae01ced9a6656c513ef7f6458e8ae24d512b851d91e",
"e8cedf03f09b9e95ad5ecb868b38c1bf8c98b76e36669ddcf2b48392b38f567f",
"6bd9236c70623557aae37b3ab12419da25d6694be94412f7c314838571e7fbed",
"8ebcb5a9bc8cbe56e35d4946d2e94ab143b621a26c08f5f04c60705caeae5662",
"c6f18c7c5a49cf74ff0a39e06737f632316511226d2e7d529d394af1968019c8",
"d373a7d968e2338e0ae847dcacff8969e13ff4018b35db36acc96951669fc599",
"c5f66d18f0f57990fe1babc74d938f0fc71593ebae3e51e7bec2ec84c412757a",
"efb964b5c9da46cfddf8c3e6b9b44d5513878c4c4ce63b3956841010992e28e9",
"b51b29835e74a403b6d8ead51fc36a818b3e44f6d62e570164771f7c6960a02c",
"4a4765330d47b15e221fa473af7c607f68aecbbb41761c8c1827fec907e4c4a7",
"7a0a7ebd4ae988310fa79ace5eb1cdb97abf949fbfe091d652b50e941b6620fa",
"93b2b1672ac550fa25c758ff031092e30077578adb25d32bc1e774ca4226d001",
"d93aac6897e9e3a5cd51ca2777a1ee1177b749395690d2f8b1ccf2704d025040",
"10780a58009b2212645e840372c090d05bb1d091a0e83152c561f19c0b914601",
"b77f268ef8be8464b9107703691a074e5dc73524dcaca4e7a46999dd68159b03",
"be8ac18717d180cc77bb61c51c7b52e2387c76be8e819cf540f93ff98004b3f7",
"8dd680ba0fb79cca7d2ece86fef4b4bc5d4468f5a80c37056f210be6e72a5344",
"39821b426f44d5f3817a73cd3a3105ac84ea6fbd96b87cfa318c130f723bf3bf",
"bd0b3602cddb45bfc17b217816ee0d2ae0999feaf0655d3d1fc772d5f7b19e25",
"d7142d788fc1d3e831cbc49c843c70f6ff0f7a24bbce13de0fd9608268e66fff",
"a45977bb70e25538b25bba6e468828cfcd2a73b7bad7b5bfb75b7de620eabfa9",
"93062cc61f2b249cac7a128f2cd8f3d0185ccf55cf1bbca1c4171b1b164e787f",
"05e20973d4062b8ee2c909f192d6a5d369d556c0044b875b9c4afee72077b4d3",
"85cf423a94c3efac15dc0d4b3c684b8f209b3aeba6c9d1fcfa038a1eef98e683",
"4f824f1d5b3f4ba49b022f832e33797903e18399ccf5b8a5843608a19f1cce6d",
"beb1030660ba1f44028d48e3ae8a3703165bff2dec4a147633f9e5b5fb688850",
"7b0ec36a12976ed41a858858b49c14ead90fa903f652c08f2345b6858237e581",
"5ff53a7ddf204959e2990d9d0b8165df703745fefeda0ecea41ac45d5f972587",
"8e66b6bc069b822fdb9f46fa710fa24577e6e25611456160440a627d0142844b",
"7226362fc96c738992397479bd787cf6f2ba7f9ab0ce99f6ab92751532431fcb",
"d0d8e1302902126b8c3f744b0d610e61b70eb1bc01b50132d21590ed59d2672e",
"5c4037b8a68c4781b0e2b6bf24801e722951c8c25f3872ff53af192241f2deec",
"377e9e763b51128659b67e49b8f3930602951e682ee3fa06675feb8a8b7c3b83",
"f115d04f8284ee5fd77fccdb24a2d9b5f9ca43d1377d2d4ddbe3992edaaa6076",
"1e4b4285c422306332c2d84756fde9316088d9434f61e5e87b86c02b349fda81",
"17279908edb806255938255b025415de166b78e8b4c60960cbe72dba24b8fbda",
"bdfe200141e290834070561e181dd7cb74186144623d98d6a2a3adcc8463d378",
"dd2f464ad679ff65c3e24f034428aef8809826d59a2701dd664d8f375ceebbcb",
"f9b087dbee57c8e3154d3d5e1218fd2045d612d539c41a970c79e2de27e93e62",
"590e8daff94a7c2d3f2c87658769d0ab0324614e3d1c3a89c1b518086d1e9dc0",
"ab45250c28ad9313ee760dc9364b3715627b4573b58412eca403be15a6df15db",
"03b4835f4daadfffee284ae3f649cd78ef8cf12757dd5f75ba1b1fa4710e6ef2",
"fd30afb596e27071f53e41b4bab007942fa9add0b934502fa0a16b0fc77eaf36",
"90c317e687e21899502c2f9b5c2f10186183b8f0b2e701f2ee94567c7e520da8",
"888558c4a3b3616d777d0186fa840153038be87670e7a4ef2a7f000712483315",
"0848c976454b6a7a21af2677f6dfab7da5054eda47cf27043a2e197b6e968061",
"deff231fa42bd2fc44901e5739fe4eeaad9938e8aefe2c1ff26ec94b2eea3b63",
"8a5a6acc0d0f893837dabdb83880787977703d324f86ac53f9194764a4e3430f",
"484174156dc8ebe58f8de99b8df718def5b44eaed4a83b079dbfedebe2c0cbf2",
"bd1947e784d8e777f2508795937b59916791e6c09369ab7343bafdd1cbae064a",
"121050a9234a6dc1808de8af5551472a3c2d58dc6376420b685f0c3e99df9280",
"77a0333cc20338f153e4159357965834236a0eb575536c2d1e8cac8d21dc8219",
"42865281e58b7ec83a2ca60b3ffb4cee0b20fb6909be52cee8801a2cd89def22",
"1e8bd6d32c1e44f21488328e6439d3d8ee5953ceb234ad7e3041530728ed47b9",
"cb09c01d520bee1878a2392e2230a65bb4a4306ab9d7f0ef38269a21e542a3a2",
"65434e685b00b1a952bf07bc1fd2d89f6879dec3985524fc2dd8e28e4bba7cbf",
"564350559ca35b31ce8334b166723b2911157f000f4bc20d86cbab81d4134968",
"d8a9f744a1aec2d66c55220544672981909a171fae7ac04a862d500554cdb9cc",
"2c9dd18192d758699c64fb8a9ccf446fb8cc186ab15db9555ef6e2e77247229c",
"d72773d273fc3a6bbda644dbbe25245cb9f29647a2e350dd673f0276e012cff2",
"e604e8b60cb2e4bdd1d47373872ed4a737705c8f7c921e6f9da04f4a25d4b312",
"1fe50794aae552688c7c69600ba55ee47461602b2705b6313c89d5960010bd29",
"dce748c190f00280386b1056014f653f70cc5ed6eac7689de7e1c34d9c3f0d4f",
"eed2c5fef3e10846a3d80bc424fd147b55319541c05271d4b2e9c096cc5eb60f",
"15c7d7dcc4d76a43f881b7cbec5926346402933a5c9bd4e6bb677c0d0d94c754",
"da162db0e0d8fd8988f52c7857b8ad3d5cbca662e835cc049616b3ec97db27c0",
"efcf7f87300849adfd96ca62cdbb002a2e8a78fa9cb342cff9e9f7b45ea67643",
"fae8cb3cf04ba5ac0293ccb088b232e0176373e1fde37dfa9843a250a947f942",
"5815bde7364b8a437391d027046c5e35dbdb8f4b84d82e23487763cc1df72fc9",
"3bc6cc859d651d4c0c67e4c8bc329479a92aa0b61828e1e561edc41c06d7278d",
"efbc9a2e8ba66ddfbb771126d23596532730e6779109762f6fd43eb016a3d3e7",
"566bf85bce4d1c91e8b727b13651ff1c900a38397a5acf6575d4e42273ef2881",
"1cabb956bfdaab07fbe055983139284be0dcc9413ac36e0a9bec79b4ea2ff305",
"0bd1b3272bb6dc2f7a7028c6a95fcae24b5c6b203394ecd0cb24a38b866c18ec",
"2b2556ed8eb45845985f28c1d2d3eb701d97a48db3878ab790e6abcd43ceb21c",
"d264f01b64fa35d77608e23d127369f1365eadc1b555784b88985cd905086978",
"e3ed8f7913a3cb12e335051000b4980a557331c7d2291065b55856528220c256",
"68a18d85014ec0ccb89dec8c9924cfadb98b7ff4828e49ff1c3c7bd0c52c15d4",
"f003e2816891214356659fa8aa4fe01f6a21189586dffd4d99ebd0da050957f0",
"38b985311df3321324ff292dcab4fa55eb71b0ae63c781b43787e487946cb575",
"221d69a73cf0fbbf90e90e3ee9cb5b36fed950ba3fe8e8ec3710c0218cf0e564",
"85bc964a7efe6c52a4af967fbb40c88f71a2bcd8c264ead871cbdacf4286148e",
"1ba20b5a62db94f622f40fe606b3ba7eb2fede94fff3f4883b546f261dbb6b29",
"52f490baeb6818ca0e18a975c87115967277e165756e18d6d0d4f6b9a0cf1e46",
"5543d4b5c6cb8a225d4370d6c10f64eb82e2712815fcafe5e6df0bf9e03b0a18",
"98ca4d851c305f2e86aa5131aa9d050af464f92a45d6b280b7a178dc8cc12a18",
"55002de76514e69e5d67c4259b10f0b5ca1d00499b6ca7c56f08eba41e7f2c3d",
"0e24c4064df7bb81ade530a3cd734fbfb817a2e9670e1cfdd2ecc6bdafaf14ff",
"441a40577c625d96239132dbe5977d491468ea1c68885c29b727373b3a71ad58",
"374fccf2e47c7ef38d496c6b401a5e07d5da4e4c12c9db547c4547de4c2480c9",
"2bc519b9aea207091be82346ea930904c21094671a79cdc540ab8aadf3c009b4",
"5f978261fc1ee9ebff50defceae97d95835833896e765e50e6e360f9de415800",
"b84feb114b31a8f75fc85fbd5644c1ca5c6f92685022d202da4f7ae2c7c3108b",
"b68ec5514498d766fbc84dd135a55df2c60808c538bf438298f15e8d8afecb1d",
"8f745ce5c2d2c1db8116c89f86293a2e798bbac87fe40eedb3591bb22d9f9da1",
"62cbec3d699ae63a501fb78ce44813b740cc1d35109401dcf26644300c39df38",
"82f71661465d11bd525f5d0c35ecb7f3779228ba79c63cd26447ed146a4a4378",
"ae06da590237ef13bfc42f30236acfcd36fcdebe47da2eacbd58e8dff4391e78",
"71020f05dd7d23bbc39dda403b183915289c8de24174636b7910b4eb5fb65f97",
"84f42fe5476d433e9bcc8020456b5fbf26114760c6cccb914d1bd86b880e71b7",
"85d3a3c37acb20ada9ef1b5e83f6e673961c4979c61e8f92e5958a390e2356d1"
],"addrStr":"1HiMoMgBaAikFHgAt3M4YJtetp4HrnsiXu",
"totalSent":33.3421,"balance":10.88690663,"totalReceived":44.22900663,"unconfirmedBalance":0}
 */
