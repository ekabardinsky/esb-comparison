/**
 * Created by ekabardinsky on 4/11/17.
 */

def baseUrl = (String) "http://${request.body.host}:${request.body.port}${body.basePath}";

def result = [
        "testCases": [
                [
                        "name"                : "inbound-soap",
                        "workerType"          : "Http",
                        "additionalParameters": [
                                "url"    : baseUrl + "/soap/inbound",
                                "method" : "POST",
                                "headers": [
                                        "SOAPAction"  : "",
                                        "Content-Type": "text/xml"
                                ],
                                "body"   : '''<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.soap.commons.magister.ekabardinsky.ru/" xmlns:pur="http://tempuri.org/PurchaseOrderSchema.xsd"><soapenv:Header/><soapenv:Body><ser:test><ser:purchaseOrder><pur:billTo><pur:city>city</pur:city><pur:country>country</pur:country><pur:name>name</pur:name><pur:state>state</pur:state><pur:street>street</pur:street><pur:zip>1</pur:zip></pur:billTo><pur:comment>comment</pur:comment><pur:confirmDate>2017-03-31</pur:confirmDate><pur:items><pur:item><pur:Item><pur:USPrice>1</pur:USPrice><pur:comment>comment</pur:comment><pur:partNum>1</pur:partNum><pur:productName>productName</pur:productName><pur:quantity>1</pur:quantity><pur:shipDate>2017-03-31</pur:shipDate></pur:Item></pur:item></pur:items><pur:orderDate>2017-03-31</pur:orderDate><pur:shipTo><pur:city>city</pur:city><pur:country>country</pur:country><pur:name>name</pur:name><pur:state>state</pur:state><pur:street>street</pur:street><pur:zip>1</pur:zip></pur:shipTo></ser:purchaseOrder></ser:test></soapenv:Body></soapenv:Envelope>'''
                        ]
                ],
                [
                        "name"                : "outbound-soap",
                        "workerType"          : "Http",
                        "additionalParameters": [
                                "url"   : baseUrl + "/soap/outbound",
                                "method": "GET"
                        ]
                ],
                [
                        "name"                : "outbound-rest",
                        "workerType"          : "Http",
                        "additionalParameters": [
                                "url"   : baseUrl + "/rest/outbound",
                                "method": "GET"
                        ]
                ],
                [
                        "name"                : "inbound-rest",
                        "workerType"          : "Http",
                        "additionalParameters": [
                                "url"   : baseUrl + "/rest/inbound",
                                "method": "GET"
                        ]
                ],
                [
                        "name"                : "outbound-ftp",
                        "workerType"          : "Http",
                        "additionalParameters": [
                                "url"   : baseUrl + "/ftp/outbound",
                                "method": "GET"
                        ]
                ]
        ]
];