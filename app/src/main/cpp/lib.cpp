#include <jni.h>
#include <string>
#include <iostream>
#include <cstdio>
#include <string>
#include <cstdlib>
#include <unistd.h>
#include <fcntl.h>
#include <cerrno>
#include <netdb.h>
#include <cstdarg>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/types.h>

#define URLSIZE 2048
using namespace std;

string getHostAddFromUrl(string strUrl) {

    char url[URLSIZE] = {0};
    strcpy(url, strUrl.c_str());
    char *strAddr = strstr(url, "http://");
    if (strAddr == NULL) {
        strAddr = strstr(url, "https://");
        if (strAddr != NULL) {
            strAddr += 8;
        }
    } else {
        strAddr += 7;
    }
    if (strAddr == NULL) {
        strAddr = url;
    }

    char *strHostAddr = (char *) malloc(strlen(strAddr) + 1);

    memset(strHostAddr, 0, strlen(strAddr) + 1);
    for (int i = 0; i < strlen(strAddr) + 1; i++) {
        if (strAddr[i] == '/') {
            break;
        } else {
            strHostAddr[i] = strAddr[i];
        }
    }
    string host = strHostAddr;
    free(strHostAddr);
    return host;
}

//Get HTTP request parameters from the HTTP request URL
string getParamFromUrl(string strUrl) {
    char url[URLSIZE] = {0};
    strcpy(url, strUrl.c_str());

    char *strAddr = strstr(url, "http://");
    if (strAddr == NULL) {
        strAddr = strstr(url, "https://");
        if (strAddr != NULL) {
            strAddr += 8;
        }
    } else {
        strAddr += 7;
    }
    if (strAddr == NULL) {
        strAddr = url;
    }

    char *strParam = (char *) malloc(strlen(strAddr) + 1);
    memset(strParam, 0, strlen(strAddr) + 1);
    int iPos = -1;
    for (int i = 0; i < strlen(strAddr) + 1; i++) {
        if (strAddr[i] == '/') {
            iPos = i;
            break;
        }
    }
    if (iPos == -1) {
        strcpy(strParam, "");
    } else {
        strcpy(strParam, strAddr + iPos + 1);
    }
    string param = strParam;
    free(strParam);
    return param;
}

string httpHeadCreate(string strMethod, string strUrl, string strData) {
    string strHost = getHostAddFromUrl(strUrl);
    string strParam = getParamFromUrl(strUrl);

    string strHttpHead;
    strHttpHead.append(strMethod);
    strHttpHead.append(" /");
    strHttpHead.append(strParam);
    strHttpHead.append(" HTTP/1.1\r\n");
    strHttpHead.append("Accept: */*\r\n");
    strHttpHead.append("Accept-Language: cn\r\n");
    strHttpHead.append("User-Agent: Mozilla/4.0\r\n");
    strHttpHead.append("Host: ");
    strHttpHead.append(strHost);
    strHttpHead.append("\r\n");
    strHttpHead.append("Cache-Control: no-cache\r\n");
    strHttpHead.append("Connection: Keep-Alive\r\n");
    if (strMethod == "POST") {
        char len[8] = {0};
        unsigned long iLen = strData.size();
        sprintf(len, "%lu", iLen);

        strHttpHead.append("Content-Type: application/x-www-form-urlencoded\r\n");
        strHttpHead.append("Content-Length: ");
        strHttpHead.append(len);
        strHttpHead.append("\r\n\r\n");
        strHttpHead.append(strData);
    }
    strHttpHead.append("\r\n\r\n");

    return strHttpHead;
}

string httpDataTransmit(string strHttpHead, int isSocFd) {
    char *buf = (char *) malloc(BUFSIZ);
    memset(buf, 0, BUFSIZ);
    char *head = (char *) strHttpHead.data();
    long ret = send(isSocFd, (void *) head, strlen(head) + 1, 0);
    if (ret < 0) {
        close(isSocFd);
        return nullptr;
    }
    while (1) {
        ret = recv(isSocFd, (void *) buf, BUFSIZ, 0);
        if (ret == 0) {
            close(isSocFd);
            free(buf);
            return nullptr;
        } else if (ret > 0) {
            string strRecv = buf;
            free(buf);
            return strRecv;
        } else if (ret < 0) {
            if (errno == EINTR || errno == EWOULDBLOCK || errno == EAGAIN) {
                continue;
            } else {
                close(isSocFd);
                free(buf);
                return nullptr;
            }
        }
    }
}

//Get the port number from the HTTP request URL
int getPortFromUrl(string strUrl) {
    int nPort = -1;
    char *strHostAddr = (char *) getHostAddFromUrl(strUrl).data();

    if (strHostAddr == NULL) {
        return -1;
    }

    char strAddr[URLSIZE] = {0};
    strcpy(strAddr, strHostAddr);
    char *strPort = strchr(strAddr, ':');
    if (strPort == NULL) {
        nPort = 80;
    } else {
        nPort = atoi(++strPort);
    }
    return nPort;
}

//Get the IP address from the Http request URL
string getIpFromUrl(string strUrl) {

    string url = getHostAddFromUrl(strUrl);
    char *strHostAddr = (char *) url.data();
    char *strAddr = (char *) malloc(strlen(strHostAddr) + 1);
    memset(strAddr, 0, strlen(strAddr) + 1);
    int nCount = 0;
    int nFlag = 0;
    for (int i = 0; i < strlen(strAddr) + 1; i++) {
        if (strHostAddr[i] == ':') {
            break;
        }
        strAddr[i] = strHostAddr[i];
        if (strHostAddr[i] == '.') {
            nCount++;
            continue;
        }
        if (nFlag == 1) {
            continue;
        }
        if ((strHostAddr[i] >= 0) || (strHostAddr[i] <= '9')) {
            nFlag = 0;
        } else {
            nFlag = 1;
        }
    }
    if (strlen(strAddr) <= 1) {
        return NULL;
    }

    //Determine whether it is a dotted decimal IP, otherwise obtain the IP through the domain name
    if ((nCount == 3) && (nFlag == 0)) {
        return strAddr;
    } else {
        struct hostent *he = gethostbyname(strAddr);
        free(strAddr);
        if (he == NULL) {
            return NULL;
        } else {
            struct in_addr **addr_list = (struct in_addr **) he->h_addr_list;
            for (int i = 0; addr_list[i] != NULL; i++) {
                return inet_ntoa(*addr_list[i]);
            }
            return NULL;
        }
    }
}

//Check whether socketFd is writable and unreadable
int socketFdCheck(const int iSockFd) {
    struct timeval timeout;
    fd_set rset, wset;
    FD_ZERO(&rset);
    FD_ZERO(&wset);
    FD_SET(iSockFd, &rset);
    FD_SET(iSockFd, &wset);
    timeout.tv_sec = 3;
    timeout.tv_usec = 500;
    int iRet = select(iSockFd + 1, &rset, &wset, NULL, &timeout);
    if (iRet > 0) {
        //Determine whether SocketFd is writable and unreadable
        int iW = FD_ISSET(iSockFd, &wset);
        int iR = FD_ISSET(iSockFd, &rset);
        if (iW && !iR) {
            char error[4] = "";
            socklen_t len = sizeof(error);
            int ret = getsockopt(iSockFd, SOL_SOCKET, SO_ERROR, error, &len);
            if (ret == 0) {
                if (!strcmp(error, "")) {
                    return iRet;//Indicates the number of descriptors that have been prepared
                }

            }

        }
    } else if (iRet == 0) {
        return 0;//Indicates timeout
    } else {
        return -1;//Select error, all descriptor sets are cleared to 0
    }
    return -2;//Other errors
}

int httpResquestExec(string strMethod, string strUrl, string strData, string &strResponse) {

    //Create HTTP protocol header
    string strHttpHead = httpHeadCreate(strMethod, strUrl, strData);
    int m_iSocketFd = socket(AF_INET, SOCK_STREAM, 0);
    if (m_iSocketFd < 0) {
        return 0;
    }

    //Bind address port
    int iPort = getPortFromUrl(strUrl);
    if (iPort < 0) {
        return 0;
    }
    string strIP = getIpFromUrl(strUrl);
    if (strIP == "") {
        return 0;
    }

    struct sockaddr_in servaddr;
    bzero(&servaddr, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(iPort);
    if (inet_pton(AF_INET, strIP.data(), &servaddr.sin_addr) <= 0) {
        close(m_iSocketFd);
        return 0;
    }

    int flags = fcntl(m_iSocketFd, F_SETFL, 0);
    if (fcntl(m_iSocketFd, F_SETFL, flags | O_NONBLOCK) == -1) {
        close(m_iSocketFd);
        return 0;
    }

    //Non-blocking connection
    int iRet = connect(m_iSocketFd, (struct sockaddr *) &servaddr, sizeof(servaddr));
    if (iRet == 0) {
        string strResult = httpDataTransmit(strHttpHead, m_iSocketFd);
        if (NULL == strResult.c_str()) {
            close(m_iSocketFd);
            return 0;
        } else {
            strResponse = strResult;
            close(m_iSocketFd);
            return 1;
        }
    } else if (iRet < 0) {
        if (errno != EINPROGRESS) {
            close(m_iSocketFd);
            return 0;
        }
    }

    iRet = socketFdCheck(m_iSocketFd);
    if (iRet > 0) {
        string strResult = httpDataTransmit(strHttpHead, m_iSocketFd);
        if (strResult.empty()) {
            close(m_iSocketFd);
            return 0;
        } else {
            close(m_iSocketFd);
            strResponse = strResult;
            return 1;
        }
    } else {
        close(m_iSocketFd);
        return 0;
    }
    close(m_iSocketFd);
    return 1;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ru_ok_android_itmohack2023_JNIActivity_nativeFunction(JNIEnv *env, jobject thiz) {
    string url = "http://www.boredapi.com/api/activity/";
    string return_msg;
    if (httpResquestExec("GET", url, "", return_msg) > 0) {
        return env->NewStringUTF(return_msg.c_str());
    }
    return env->NewStringUTF(string("error").c_str());
}