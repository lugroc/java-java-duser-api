# AWS Deployment Checklist — $0/month

## 1. Billing Alarm (BEFORE anything else)
- [ ] Go to **Billing → Budgets → Create budget** ($0 monthly, email alert)
- [ ] Go to **CloudWatch → Alarms → Billing → Create alarm** ($0.01 threshold)
- [ ] Enable **Cost Explorer** to track spend

## 2. Monolith — EC2 t3.micro + Docker

### Launch EC2
- [ ] Region: **us-east-1** (free tier for all services)
- [ ] Instance type: **t3.micro** only (1 vCPU, 1 GB RAM)
- [ ] AMI: **Amazon Linux 2023** (free)
- [ ] Root volume: **30GB gp3** (free tier limit)
- [ ] Key pair: Create or use existing
- [ ] Security group: **SSH (22) + port 8080** from your IP only
- [ ] Advanced details → **Credit specification: standard** (t3.micro has free CPU credits)
- [ ] **Do NOT** add Load Balancer, NAT Gateway, or Elastic IP (unless attached to running instance)

### Deploy
```bash
ssh -i your-key.pem ec2-user@<public-ip>
sudo dnf install docker -y
sudo systemctl enable --now docker
sudo usermod -aG docker ec2-user   # re-login after this
```

```bash
# On your local machine:
cd aws-monolith
docker compose up -d --build
docker save aws-monolith-aws-app | gzip > app.tar.gz
scp -i your-key.pem app.tar.gz ec2-user@<ip>:
scp -i your-key.pem docker-compose.yml ec2-user@<ip>:
```

```bash
# On EC2:
gunzip app.tar.gz
docker load < app.tar
docker compose up -d
```

### Verify
```bash
curl http://localhost:8080/api/v1/sessions/validate/00000000-0000-0000-0000-000000000000
```

## 3. Lambda — Contact Form

```bash
cd aws-lambda
sam build
sam deploy --guided
```

**`sam deploy` options to avoid charges:**
- `Stack Name`: portfolio-contact
- `AWS Region`: us-east-1
- Confirm before changes: Y
- **Allow SAM CLI IAM role creation**: Y (creates free IAM roles)
- **`Disable rollback`**: N
- **`ContactFunction may not have authorization defined`**: Y (API is public — okay for contact form)

**Do NOT select a VPC during deployment** — that creates a NAT Gateway ($32/mo).

---

### 4. Frontend (Vercel)

After deploy, set Vercel env variables:

| Variable | Value |
|---|---|
| `VITE_CONTACT_API_URL` | `https://xxx.execute-api.us-east-1.amazonaws.com/Prod/contact` |

Then redeploy the frontend on Vercel.

The monolith API base URL stays in `vercel.json` rewrites → your EC2 public IP.

---

### 5. Stop when not using (optional, saves credits)

Stop (not terminate) the EC2 instance from the console — EBS storage still costs ~$3/mo but within free tier. Lambda/DynamoDB/API Gateway cost $0 at this scale.

---

### Cost traps to NEVER click:
- ❌ CloudFront distribution outside free tier
- ❌ Load Balancer (ALB/NLB) of any size
- ❌ NAT Gateway
- ❌ Elastic IP not attached to a running instance
- ❌ RDS outside db.t3.micro
- ❌ t3.micro with `--cpu-options standard` (default is `t3-unlimited` which costs extra)
- ❌ EC2 in a non-free tier instance family (t3a, t4g, m7g — all outside free tier)
- ❌ EBS snapshot storage over 30GB

If you ever see anything other than $0.00 in the billing dashboard, stop everything and inspect.
