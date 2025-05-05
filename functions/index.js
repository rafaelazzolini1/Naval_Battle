const functions = require('firebase-functions');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');

admin.initializeApp();

// Configurar o transporte de e-mail com Gmail
const transporter = nodemailer.createTransport({
    service: 'Gmail',
    auth: {
        user: process.env.GMAIL_EMAIL_ADDRESS,
        pass: process.env.GMAIL_APP_PASSWORD
    }
});

/**
 * Função acionada quando um novo documento de jogo é criado.
 * Envia um e-mail ao usuário se ele for o vencedor.
 */
exports.sendWinnerEmail = functions.firestore
    .document('games/{userId}/matches/{matchId}')
    .onCreate(async (snap, context) => {
        const gameData = snap.data();
        const userId = context.params.userId;

        // Verificar se o usuário venceu
        if (gameData.winner !== userId) {
            console.log(`Usuário ${userId} não venceu. Nenhum e-mail será enviado.`);
            return null;
        }

        const userEmail = gameData.userEmail;
        const winnerScore = gameData.winnerScore;
        const totalMoves = gameData.totalMoves;

        // Configurar o conteúdo do e-mail
        const mailOptions = {
            from: 'Naval Battle Game <your-email@gmail.com>',
            to: userEmail,
            subject: 'Congratulations! You Won the Naval Battle!',
            html: `
                <h2>Congratulations, Champion!</h2>
                <p>You have won a match in Naval Battle!</p>
                <p><strong>Your Score:</strong> ${winnerScore}</p>
                <p><strong>Total Moves:</strong> ${totalMoves}</p>
                <p>Keep playing and aim for more victories!</p>
                <p>Best regards,<br>Naval Battle Team</p>
            `
        };

        try {
            await transporter.sendMail(mailOptions);
            console.log(`E-mail enviado para ${userEmail} com sucesso.`);
            return null;
        } catch (error) {
            console.error(`Erro ao enviar e-mail para ${userEmail}:`, error);
            return null;
        }
    });