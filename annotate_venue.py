# Import the elasticsearch, watson_developer_cloud, boto3, foursquare and other support libraries.

from __future__ import print_function
from elasticsearch import Elasticsearch
from watson_developer_cloud import VisualRecognitionV3
import json
import boto3
import certifi
import foursquare

# Initialize the foursquare client, rekognition client, visual recognition client and elasticsearch. The keys
#and the elasticsearch domain url have been removed here for security reasons.

print('Loading function')

foursquare_client = foursquare.Foursquare(client_id='',
                               client_secret='')

rekognition_client = boto3.client('rekognition',
                      region_name='us-west-2',
                      aws_access_key_id='',
                      aws_secret_access_key='')

visual_recognition = VisualRecognitionV3('2016-05-20', api_key='')

es = Elasticsearch([''])

# The lambda handler that receive the notification from the SNS. The message consist of the GPS
# coordinates of the venue, the url array of the images uploaded to S3 url and the audio amplitude.
# It calls the annotate_venue with the venue as the parameter.

def lambda_handler(event, context):
    #print("Received event: " + json.dumps(event, indent=2))
    message = event['Records'][0]['Sns']['Message']
    print("From SNS: " + message)
    venue = json.loads(message)
    print(venue['ll'])
    print(venue['images'])
    print(venue['audio_amplitude'])
    annotate_venue(venue)
    return message

# The noise_level classifies the venue as ‘very silent’, ‘silent’, ‘normal’, ‘noisy’, ‘very noisy’ depending
# upon the amplitude of the noise level.

def noise_level(amplitude):
    if amplitude <= 1000:
        return "Very Silent"
    elif amplitude >1000 and amplitude <= 15000:
        return "Silent"
    elif amplitude >15000 and amplitude <= 30000:
        return "Normal"
    elif amplitude >30000 and amplitude <= 45000:
        return "Noisy"
    elif amplitude > 45000:
        return "Very Noisy"

# Call the foursquare venue search api, AWS Rekognition api, IBM Watson visual rekognition api.   
def annotate_venue(venue):
    foursquare_response = foursquare_client.venues.search(params={'ll': venue['ll'], 'limit': '1'})
    
    for image in venue['images']:
        rekognition_response = rekognition_client.detect_labels(
            Image={
                'S3Object': {
                'Bucket': '',
                'Name': image[67:]
            }
        }
        )

        visual_recognition_classify_response = visual_recognition.classify(
            images_url=image)
        visual_recognition_text_response = visual_recognition.recognize_text(
            images_url=image)

        words = []
        visual_recognition_classes = []
        # Get the venue name
        venue_name = foursquare_response['venues'][0]['name']
        # Get the rekognition labels
        rekognition_labels = rekognition_response['Labels']
        # Get the visual rekognition classes
        if 'classifiers' in visual_recognition_text_response['images'][0]:
            visual_recognition_classes = visual_recognition_classify_response['images'][0]['classifiers'][0]['classes']
        # Get the visual rekognition words
        if 'words' in visual_recognition_text_response['images'][0]:
            words = visual_recognition_text_response['images'][0]['words']

        # Remove the duplicate labels from AWS Rekognition api and IBM Watson visul recognition api.
        duplicate = False
        for vr_class in visual_recognition_classes:
            for label in rekognition_labels:
                if vr_class['class'].lower() == label['Name'].lower():
                    duplicate = True
            if duplicate == False:
                rekognition_labels.append({'Name': vr_class['class'], 'Confidence': vr_class['score']})
            duplicate = False

        text_labels = []
        for word in words:
            text_labels.append(word['word'])

        # Determine the noise category for the venue depending upon the amplitude
        noise_category = noise_level(int(venue['audio_amplitude']))
        print(noise_category)

        # Build the document to index into elasticsearch
        doc = {
            'venue': venue_name,
            'location':venue['ll'],
            'url': image,
            'annotations': rekognition_labels,
            'text_labels': text_labels,
            'noise_level': noise_category
        }
        print(doc)
        # Index the document into elasticsearch
        res = es.index(index='venue_annotations', doc_type='venues', body=doc)
