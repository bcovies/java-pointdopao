CREATE TABLE "compra" (
  "id" serial NOT NULL,
  "id_usuario" integer NOT NULL,
  "valor_total" numeric (7,2) NOT NULL,
  "hora_compra" TIMESTAMP, 
  PRIMARY KEY (id),
  FOREIGN KEY (id_usuario) REFERENCES usuario (id)
);

