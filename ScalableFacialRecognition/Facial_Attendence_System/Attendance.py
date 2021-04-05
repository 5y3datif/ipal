import cv2
import datetime
from FaceRecognition.Image_Services import ImageServices

class AttendanceManagement():
    # def __init__(self):
    #     self.image = image
    #     self.MA = self.MarkAttendance(self)

    def choice(self):
        option = input('Enter 1 for Still Image, 2 for Live Stream.')
        print(option)
        if option == '1':
            self.StillImage()
        elif option == '2':
            self.LiveStream()
        else:
            print('Invalid Input. Plz enter correct option.....')
            self.choice()

    def StillImage(self):
        image = cv2.imread('Ayub.jpg')
        self.MarkAttendance(image)

    def LiveStream(self):
        cap = cv2.VideoCapture(0)
        _, im = cap.read()
        cap.release()
        self.MarkAttendance(im)


    def MarkAttendance(self, img):
        obj = ImageServices()
        print('Image Received.......')
        x1,y1,x2,y2 = obj.FaceDetection(img)
        print('Face Extracted.......')
        face = img[y1:y2, x1:x2]
        emb = obj.Calc_Embedding(face)
        print('Embedding Calculated.........')
        identity = obj.Recognize(emb, 3)
        print('Identified.......as', identity)
        with open('Attendance.csv', 'a') as f:
            now = datetime.datetime.now()
            f.write(f'{identity}, {now}'+'\n')
            f.close()
        print('Attendance Marked.....')
























