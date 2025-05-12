const express = require("express");
const nodemailer = require("nodemailer");
const app = express();

app.use(express.json());

const transporter = nodemailer.createTransport({
  service: "Gmail",
  auth: {
    user: process.env.EMAIL_USER,
    pass: process.env.EMAIL_PASS
  }
});

app.post("/send-email", async (req, res) => {
  const { to, subject, html } = req.body;

  try {
    await transporter.sendMail({
      from: `"Naval Battle" <${process.env.EMAIL_USER}>`,
      to,
      subject,
      html
    });
    res.status(200).send({ success: true, message: "E-mail enviado!" });
  } catch (error) {
    console.error(error);
    res.status(500).send({ success: false, message: "Erro ao enviar e-mail." });
  }
});

const PORT = process.env.PORT || 10000;
app.listen(PORT, () => console.log(`Servidor rodando na porta ${PORT}`));
