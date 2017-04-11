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
                                "url"    : (String) "http://${request.body.host}:${request.body.soapPort}${body.basePath}" + "/soap/inbound",
                                "method" : "POST",
                                "headers": [
                                        "SOAPAction"  : "",
                                        "Content-Type": "text/xml"
                                ],
                                "body"   : '''<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.soap.commons.magister.ekabardinsky.ru/" xmlns:pur="http://tempuri.org/PurchaseOrderSchema.xsd"><soapenv:Header/><soapenv:Body><ser:test><arg0 orderDate="2017-03-31" confirmDate="2017-03-31"><pur:shipTo country="country"><pur:name>name</pur:name><pur:street>street</pur:street><pur:city>city</pur:city><pur:state>state</pur:state><pur:zip>1</pur:zip></pur:shipTo><pur:billTo country="country"><pur:name>name</pur:name><pur:street>name</pur:street><pur:city>city</pur:city><pur:state>state</pur:state><pur:zip>1</pur:zip></pur:billTo><pur:comment>comment</pur:comment><pur:items><pur:item partNum="1"><pur:productName>productName</pur:productName><pur:quantity>1</pur:quantity><pur:USPrice>1</pur:USPrice><pur:comment>comment</pur:comment><pur:shipDate>2017-03-31</pur:shipDate></pur:item></pur:items></arg0></ser:test></soapenv:Body></soapenv:Envelope>'''
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