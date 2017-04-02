FROM stilliard/pure-ftpd
#pre installed user 'bob' with password 'bob'
COPY pureftpd.passwd /etc/pure-ftpd/passwd/pureftpd.passwd
COPY data/ /home/ftpusers/user


RUN pass | pure-pw useradd user -f /etc/pure-ftpd/passwd/pureftpd.passwd -m -u ftpuser -d /home/ftpusers/user